CREATE TABLE shared_schema.appuser
(
    userid integer NOT NULL DEFAULT nextval('shared_schema.appuser_userid_seq'::regclass),
    logonname character varying(255) COLLATE pg_catalog."default" NOT NULL,
    hashpassword character varying(1024) COLLATE pg_catalog."default" NOT NULL,
    firstname character varying(64) COLLATE pg_catalog."default" NOT NULL,
    lastname character varying(64) COLLATE pg_catalog."default" NOT NULL,
    emailid character varying(255) COLLATE pg_catalog."default" NOT NULL,
    authenticationtype character varying(100) COLLATE pg_catalog."default" NOT NULL DEFAULT 'DB_PWD'::character varying,
    singlesignonid character varying(100) COLLATE pg_catalog."default",
    invalidloginattempts integer,
    lastlogintime timestamp with time zone,
    lastpasswordchangetime timestamp with time zone,
    status character(1) COLLATE pg_catalog."default" NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT appuser_pkey PRIMARY KEY (userid),
    CONSTRAINT appuser_emailid_key UNIQUE (emailid),
    CONSTRAINT appuser_logonname_key UNIQUE (logonname)
)

TABLESPACE pg_default;

ALTER TABLE shared_schema.appuser
    OWNER to familytree_master;

REVOKE ALL ON TABLE shared_schema.appuser FROM familytree_app_role;

GRANT DELETE, INSERT, SELECT, UPDATE ON TABLE shared_schema.appuser TO familytree_app_role;

GRANT ALL ON TABLE shared_schema.appuser TO familytree_master;

COMMENT ON TABLE shared_schema.appuser
    IS 'Table for application users';

COMMENT ON COLUMN shared_schema.appuser.userid
    IS 'Unique numeric id for user. This value will be used by other tables for reference.';

COMMENT ON COLUMN shared_schema.appuser.logonname
    IS 'Unique logon name to identify the user. This can be alphanumaric id or email id';

COMMENT ON COLUMN shared_schema.appuser.hashpassword
    IS 'Encrypted (one way hash) password for this user';

COMMENT ON COLUMN shared_schema.appuser.firstname
    IS 'Users first name';

COMMENT ON COLUMN shared_schema.appuser.lastname
    IS 'Users last name';

COMMENT ON COLUMN shared_schema.appuser.emailid
    IS 'Unique User emailid';

COMMENT ON COLUMN shared_schema.appuser.authenticationtype
    IS 'How this user is authenticated in application. Options are DB_PWD, LDAP, OAUTH';

COMMENT ON COLUMN shared_schema.appuser.singlesignonid
    IS 'Is used to authenticate user with single sign on application like Free IPA or Active Directory. In most case, this will be same as logonname';

COMMENT ON COLUMN shared_schema.appuser.invalidloginattempts
    IS 'This columns maintain number of invalid attempts for this user.
On every successful login this number get reset to zero.
If this number goes above threshold invalid login attempt, user status will get updtaed to locked.';

COMMENT ON COLUMN shared_schema.appuser.lastlogintime
    IS 'Time when this user is successfully login to application';

COMMENT ON COLUMN shared_schema.appuser.status
    IS 'Status of the user.
A - Active
I - Inactive
L - Lock
Only active user can login and perform transactions.';

COMMENT ON COLUMN shared_schema.appuser.createdby
    IS 'User id for user who created this record';

COMMENT ON COLUMN shared_schema.appuser.createddate
    IS 'Time when this record was created';

COMMENT ON COLUMN shared_schema.appuser.lastupdatedby
    IS 'User id for user who last updated this record';

COMMENT ON COLUMN shared_schema.appuser.lastupdateddate
    IS 'Time when this record was last updated';