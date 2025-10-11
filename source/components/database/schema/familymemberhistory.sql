-- Table: shared_schema.familymemberhistory

-- DROP TABLE IF EXISTS shared_schema.familymemberhistory;

CREATE TABLE IF NOT EXISTS shared_schema.familymemberhistory
(
    historyid SERIAL NOT NULL,
    operation character varying(10) COLLATE pg_catalog."default" NOT NULL,
    memberid integer NOT NULL,
    familyid integer NOT NULL,
    logonname character varying(150) COLLATE pg_catalog."default",
    headoffamily boolean NOT NULL DEFAULT false,
    firstname character varying(100) COLLATE pg_catalog."default" NOT NULL,
    firstnameinhindi character varying(100) COLLATE pg_catalog."default",
    lastname character varying(100) COLLATE pg_catalog."default" NOT NULL,
    maidenlastname character varying(100) COLLATE pg_catalog."default",
    nickname character varying(100) COLLATE pg_catalog."default",
    nicknameinhindi character varying(100) COLLATE pg_catalog."default",
    gender character varying(10) COLLATE pg_catalog."default",
    birthday smallint,
    birthmonth smallint NOT NULL,
    birthyear smallint NOT NULL,
    maritalstatus character varying(20) COLLATE pg_catalog."default" NOT NULL,
    weddingdate date,
    dateofdeath date,
    phone character varying(20) COLLATE pg_catalog."default",
    isphonewhatsappregistered boolean NOT NULL DEFAULT false,
    email character varying(100) COLLATE pg_catalog."default",
    addresssameasfamily boolean NOT NULL DEFAULT true,
    memberaddressid integer,
    educationdetails character varying(255) COLLATE pg_catalog."default",
    occupation character varying(255) COLLATE pg_catalog."default",
    hobby character varying(255) COLLATE pg_catalog."default",
    membersearchtext character varying(500) COLLATE pg_catalog."default",
    profileimage character varying(255) COLLATE pg_catalog."default" NOT NULL,
    profileimagethumbnail character varying(255) COLLATE pg_catalog."default" NOT NULL,
    imagelastupdated timestamp with time zone,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT familymemberhistory_pkey PRIMARY KEY (historyid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.familymemberhistory
    OWNER to postgres;

COMMENT ON TABLE shared_schema.familymemberhistory
    IS 'Table to store historical versions of family member profile records.  
The main table always contains the latest version, while this table stores only previous versions of records.  
No entry is created here when a record is first inserted into the main table.  
For column descriptions, refer to the familymember table.';

COMMENT ON COLUMN shared_schema.familymemberhistory.historyid
    IS 'Unique history id to maintain history of changes';

COMMENT ON COLUMN shared_schema.familymemberhistory.operation
    IS 'Operation type i.e. UPDATE when updating main table, DELETE when deleting from main table';