-- =============================================================================
-- Data Masking Script — Family Tree Application
-- =============================================================================
-- Run this script AFTER importing a production database dump into your dev DB.
--
-- Step 1 — Clear history, registration queue, and transient tables:
--   familymemberhistory, familyhistory, familymemberregistration,
--   familyregistrationrequest, txtsessiontxn, txtsession, otpverification,
--   otpverificationhistory are wholly truncated — stale PII copies / live
--   tokens / IP addresses with no dev value.
--
-- Step 2 — Purge samajid = 1 data:
--   samajid = 1 is the samaj the app is configured against. This removes all
--   family/member/address/user data belonging to samajid = 1:
--     - memberrelationship rows for samajid = 1 members (FK to familymember,
--       must go first)
--     - familymember, then family rows for samajid = 1 (familymember has the
--       FK to family, so it must be deleted first)
--     - address rows left with no remaining family/familymember reference
--       (computed AFTER the deletes above, so samajid = 1's now-orphaned
--       addresses are the ones caught; addresses still used by other samaj
--       data are left alone)
--     - mstuserrolemap rows, then appuser rows, for accounts no longer linked
--       to any familymember.logonname (i.e. accounts that only belonged to
--       samajid = 1), excluding userid 1 and 2 (system / admin accounts)
--
-- Step 3 — appuser: reset security-sensitive fields on all remaining accounts
--   - hashpassword                          → fixed Argon2id hash for `myDevPassword`
--   - singlesignonid                        → NULL
--   - invalidloginattempts                  → 0
--   - lastlogintime / lastpasswordchangetime → NULL
--
-- DEV PASSWORD NOTE:
--   All remaining user accounts are reset to `myDevPassword`.
--
-- =============================================================================

BEGIN;

-- =============================================================================
-- 1. Clear history, registration queue, and transient tables
--    History and registration tables hold stale PII copies; they have no dev
--    value and are safest to wipe entirely.
--    txtsessiontxn has a FK to txtsession so it must be cleared first.
--    OTP and session tables contain live tokens and IP addresses.
-- =============================================================================

TRUNCATE shared_schema.familymemberhistory;
TRUNCATE shared_schema.familyhistory;
TRUNCATE shared_schema.familymemberregistration;
TRUNCATE shared_schema.familyregistrationrequest CASCADE;

TRUNCATE shared_schema.txtsessiontxn;
TRUNCATE shared_schema.txtsession CASCADE;
TRUNCATE shared_schema.otpverification;
TRUNCATE shared_schema.otpverificationhistory;

-- =============================================================================
-- 2. Purge samajid = 1 data (family, member, address, user)
-- =============================================================================

-- Delete memberrelationship rows for samajid = 1 members first — both
-- memberid and relatedmemberid FK to familymember with ON DELETE RESTRICT.
delete from shared_schema.memberrelationship where memberid in
    (SELECT memberid
    FROM shared_schema.familymember where familyid in (select familyid from shared_schema.family where samajid = 1)
    );

-- Delete familymember for samajid = 1, so the app doesn't try to display them.
delete from shared_schema.familymember fm
    using shared_schema.family f where f.familyid = fm.familyid
    and f.samajid = 1;

-- Delete family for samajid = 1, so the app doesn't try to display them.
delete from shared_schema.family f
    where f.samajid = 1;

-- Delete unused address — run after the family/familymember deletes above so
-- addresses only referenced by the now-deleted samajid = 1 rows are caught.
delete from shared_schema.address a where a.addressid not in (
	select f.addressid from shared_schema.family f where f.addressid is not null
	union
	select m.memberaddressid from shared_schema.familymember m
);


-- Delete mstuserrolemap rows for orphaned users first — userid FKs to
-- appuser with ON DELETE RESTRICT, so this must precede the appuser delete.
delete from shared_schema.mstuserrolemap
where userid in (
    select au.userid
    from shared_schema.appuser au
    where au.userid not in (1, 2)
      and not exists (
        select 1 from shared_schema.familymember m
        where m.logonname = au.logonname
      )
);

-- Delete appuser for samajid = 1: after the familymember delete above, any
-- account with no remaining familymember.logonname match only belonged to
-- samajid = 1. userid 1 and 2 (system / admin) are kept regardless.
delete from shared_schema.appuser au
where au.logonname not in (
	select logonname from shared_schema.familymember m where m.logonname is not null
)
and userid not in (1,2);




-- =============================================================================
-- 3. appuser — reset security-sensitive fields on all remaining accounts
-- =============================================================================

UPDATE shared_schema.appuser
SET
    hashpassword           = '$argon2id$v=19$m=16384,t=2,p=1$7arh7YIIjnMLvW0KrKxw1A$+Z9UIdQ8MUuf40V38N8v8ZZNAEGY59FsItIczNL5+JQ',
    singlesignonid         = NULL,
    invalidloginattempts   = 0,
    lastlogintime          = NULL,
    lastpasswordchangetime = NULL;


COMMIT;
