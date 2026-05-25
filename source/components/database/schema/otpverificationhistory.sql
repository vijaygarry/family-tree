CREATE TABLE shared_schema.otpverificationhistory (
    seqid bigint NOT NULL,
    emailid character varying(255) NOT NULL,
    otptype character varying(50) NOT NULL,
    hashotpcode character varying(1024) NOT NULL,
    requestid character varying(55) NOT NULL,
    status character varying(20) DEFAULT 'Pending'::character varying NOT NULL,
    verifiedat timestamp with time zone,
    expirydate timestamp with time zone NOT NULL,
    attempts integer DEFAULT 0 NOT NULL,
    lastattemptdate timestamp with time zone,
    createddate timestamp with time zone NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);

ALTER TABLE shared_schema.otpverificationhistory OWNER TO familytree_master;

COMMENT ON TABLE shared_schema.otpverificationhistory IS 'Table for storing OTP verification details';

COMMENT ON COLUMN shared_schema.otpverificationhistory.seqid IS 'Unique identifier for each OTP verification entry';

COMMENT ON COLUMN shared_schema.otpverificationhistory.emailid IS 'Email address associated with the OTP';

COMMENT ON COLUMN shared_schema.otpverificationhistory.otptype IS 'Type of OTP (e.g., SignUp, ForgotPassword)';

COMMENT ON COLUMN shared_schema.otpverificationhistory.hashotpcode IS 'Encrypted (one way hash) OTP code for this user';

COMMENT ON COLUMN shared_schema.otpverificationhistory.requestid IS 'Identifier for the OTP request';

COMMENT ON COLUMN shared_schema.otpverificationhistory.status IS 'Current status of the OTP (e.g., Pending, Verified, Expired)';

COMMENT ON COLUMN shared_schema.otpverificationhistory.verifiedat IS 'Timestamp when the OTP was verified';

COMMENT ON COLUMN shared_schema.otpverificationhistory.expirydate IS 'Timestamp when the OTP expires';

COMMENT ON COLUMN shared_schema.otpverificationhistory.attempts IS 'Number of attempts made to verify the OTP';

COMMENT ON COLUMN shared_schema.otpverificationhistory.lastattemptdate IS 'Timestamp of the last attempt to verify the OTP';

COMMENT ON COLUMN shared_schema.otpverificationhistory.createddate IS 'Timestamp when the OTP verification entry was created';

COMMENT ON COLUMN shared_schema.otpverificationhistory.lastupdateddate IS 'Timestamp when the OTP verification entry was last updated';

CREATE SEQUENCE shared_schema.otpverificationhistory_seqid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.otpverificationhistory_seqid_seq OWNER TO familytree_master;

ALTER SEQUENCE shared_schema.otpverificationhistory_seqid_seq OWNED BY shared_schema.otpverificationhistory.seqid;

ALTER TABLE ONLY shared_schema.otpverificationhistory ALTER COLUMN seqid SET DEFAULT nextval('shared_schema.otpverificationhistory_seqid_seq'::regclass);

ALTER TABLE ONLY shared_schema.otpverificationhistory
    ADD CONSTRAINT otpverificationhistory_pkey PRIMARY KEY (seqid);

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.otpverificationhistory TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.otpverificationhistory_seqid_seq TO familytree_app_role;
