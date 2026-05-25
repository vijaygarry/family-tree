CREATE TABLE shared_schema.familymemberhistory (
    historyid integer NOT NULL,
    operation character varying(10) NOT NULL,
    memberid integer NOT NULL,
    familyid integer NOT NULL,
    samajid smallint NOT NULL,
    logonname character varying(150),
    headoffamily boolean DEFAULT false NOT NULL,
    firstname character varying(100) NOT NULL,
    firstnameinhindi character varying(100),
    lastname character varying(100) NOT NULL,
    maidenlastname character varying(100),
    nickname character varying(100),
    nicknameinhindi character varying(100),
    gender character varying(10),
    birthday smallint,
    birthmonth smallint NOT NULL,
    birthyear smallint NOT NULL,
    maritalstatus character varying(20) NOT NULL,
    weddingdate date,
    dateofdeath date,
    phone character varying(20),
    isphoneverified boolean DEFAULT false NOT NULL,
    isphonewhatsappregistered boolean DEFAULT false NOT NULL,
    email character varying(100),
    isemailverified boolean DEFAULT false NOT NULL,
    addresssameasfamily boolean DEFAULT true NOT NULL,
    memberaddressid integer,
    educationdetails character varying(255),
    occupation character varying(255),
    hobby character varying(255),
    membersearchtext character varying(500),
    profileimage character varying(255) NOT NULL,
    profileimagethumbnail character varying(255) NOT NULL,
    imagelastupdated timestamp with time zone,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);

ALTER TABLE shared_schema.familymemberhistory OWNER TO postgres;

COMMENT ON TABLE shared_schema.familymemberhistory IS 'Table to store historical versions of family member profile records.  
The main table always contains the latest version, while this table stores only previous versions of records.  
No entry is created here when a record is first inserted into the main table.  
For column descriptions, refer to the familymember table.';

COMMENT ON COLUMN shared_schema.familymemberhistory.historyid IS 'Unique history id to maintain history of changes';

COMMENT ON COLUMN shared_schema.familymemberhistory.operation IS 'Operation type i.e. UPDATE when updating main table, DELETE when deleting from main table';

CREATE SEQUENCE shared_schema.familymemberhistory_historyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.familymemberhistory_historyid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.familymemberhistory_historyid_seq OWNED BY shared_schema.familymemberhistory.historyid;

ALTER TABLE ONLY shared_schema.familymemberhistory ALTER COLUMN historyid SET DEFAULT nextval('shared_schema.familymemberhistory_historyid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familymemberhistory
    ADD CONSTRAINT familymemberhistory_pkey PRIMARY KEY (historyid);

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familymemberhistory TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familymemberhistory_historyid_seq TO familytree_app_role;
