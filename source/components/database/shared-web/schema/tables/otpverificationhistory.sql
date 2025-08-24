CREATE TABLE IF NOT EXISTS shared_schema.otpverificationhistory
(
    seqid bigserial NOT NULL,
    emailid character varying(255) COLLATE pg_catalog."default" NOT NULL,
    otptype character varying(50) COLLATE pg_catalog."default" NOT NULL,
    hashotpcode character varying(1024) COLLATE pg_catalog."default" NOT NULL,
    requestid character varying(55) COLLATE pg_catalog."default" NOT NULL,
    status character varying(20) COLLATE pg_catalog."default" NOT NULL DEFAULT 'Pending'::character varying,
    verifiedat timestamp with time zone,
    expirydate timestamp with time zone NOT NULL,
    attempts integer NOT NULL DEFAULT 0,
    lastattemptdate timestamp with time zone,
    createddate timestamp with time zone NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT otpverificationhistory_pkey PRIMARY KEY (seqid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.otpverificationhistory
    OWNER to familytree_master;

COMMENT ON TABLE shared_schema.otpverificationhistory
    IS 'Table for storing OTP verification details';

COMMENT ON COLUMN shared_schema.otpverificationhistory.seqid
    IS 'Unique identifier for each OTP verification entry';

COMMENT ON COLUMN shared_schema.otpverificationhistory.emailid
    IS 'Email address associated with the OTP';

COMMENT ON COLUMN shared_schema.otpverificationhistory.otptype
    IS 'Type of OTP (e.g., SignUp, ForgotPassword)';

COMMENT ON COLUMN shared_schema.otpverificationhistory.hashotpcode
    IS 'Encrypted (one way hash) OTP code for this user';

COMMENT ON COLUMN shared_schema.otpverificationhistory.requestid
    IS 'Identifier for the OTP request';

COMMENT ON COLUMN shared_schema.otpverificationhistory.status
    IS 'Current status of the OTP (e.g., Pending, Verified, Expired)';

COMMENT ON COLUMN shared_schema.otpverificationhistory.verifiedat
    IS 'Timestamp when the OTP was verified';

COMMENT ON COLUMN shared_schema.otpverificationhistory.expirydate
    IS 'Timestamp when the OTP expires';

COMMENT ON COLUMN shared_schema.otpverificationhistory.attempts
    IS 'Number of attempts made to verify the OTP';

COMMENT ON COLUMN shared_schema.otpverificationhistory.lastattemptdate
    IS 'Timestamp of the last attempt to verify the OTP';

COMMENT ON COLUMN shared_schema.otpverificationhistory.createddate
    IS 'Timestamp when the OTP verification entry was created';

COMMENT ON COLUMN shared_schema.otpverificationhistory.lastupdateddate
    IS 'Timestamp when the OTP verification entry was last updated';
