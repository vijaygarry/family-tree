-- Table: shared_schema.familyhistory

-- DROP TABLE IF EXISTS shared_schema.familyhistory;

CREATE TABLE IF NOT EXISTS shared_schema.familyhistory
(
    historyid SERIAL NOT NULL,
    operation character varying(10) NOT NULL,
    familyid integer NOT NULL,
    familyname character varying(120) NOT NULL,
    familynameinhindi character varying(120),
    gotra character varying(100) NOT NULL,
    addressid integer,
    region character varying(200),
    phone character varying(20),
    isphonewhatsappregistered boolean NOT NULL DEFAULT false,
    email character varying(100),
    familysearchtext character varying(500),
    active boolean NOT NULL DEFAULT true,
    familyimage character varying(120),
    imagelastupdated timestamp with time zone NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT familyhistory_pkey PRIMARY KEY (historyid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.familyhistory
    OWNER to postgres;

COMMENT ON TABLE shared_schema.familyhistory
    IS 'Table to store historical versions of family records.  
The main table always contains the latest version, while this table stores only previous versions of records.  
No entry is created here when a record is first inserted into the main table.  
For column descriptions, refer to the family table.';

COMMENT ON COLUMN shared_schema.familyhistory.historyid
    IS 'Unique history id to maintain history of changes';

COMMENT ON COLUMN shared_schema.familyhistory.operation
    IS 'Operation type i.e. UPDATE when updating main table, DELETE when deleting from main table';