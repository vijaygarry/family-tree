-- Table: shared_schema.otpverification

-- DROP TABLE IF EXISTS shared_schema.otpverification;

CREATE TABLE IF NOT EXISTS shared_schema.otpverification
(
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
    CONSTRAINT otpverification_pkey PRIMARY KEY (emailid, otptype)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.otpverification
    OWNER to familytree_master;

COMMENT ON TABLE shared_schema.otpverification
    IS 'Table for storing OTP verification details';

COMMENT ON COLUMN shared_schema.otpverification.emailid
    IS 'Email address associated with the OTP';

COMMENT ON COLUMN shared_schema.otpverification.otptype
    IS 'Type of OTP (e.g., SignUp, ForgotPassword)';

COMMENT ON COLUMN shared_schema.otpverification.hashotpcode
    IS 'Encrypted (one way hash) OTP code for this user';

COMMENT ON COLUMN shared_schema.otpverification.requestid
    IS 'Identifier for the OTP request';

COMMENT ON COLUMN shared_schema.otpverification.status
    IS 'Current status of the OTP (e.g., Pending, Verified, Expired)';

COMMENT ON COLUMN shared_schema.otpverification.verifiedat
    IS 'Timestamp when the OTP was verified';

COMMENT ON COLUMN shared_schema.otpverification.expirydate
    IS 'Timestamp when the OTP expires';

COMMENT ON COLUMN shared_schema.otpverification.attempts
    IS 'Number of attempts made to verify the OTP';

COMMENT ON COLUMN shared_schema.otpverification.lastattemptdate
    IS 'Timestamp of the last attempt to verify the OTP';

COMMENT ON COLUMN shared_schema.otpverification.createddate
    IS 'Timestamp when the OTP verification entry was created';

COMMENT ON COLUMN shared_schema.otpverification.lastupdateddate
    IS 'Timestamp when the OTP verification entry was last updated';