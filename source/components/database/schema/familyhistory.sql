CREATE TABLE shared_schema.familyhistory (
    historyid integer NOT NULL,
    operation character varying(10) NOT NULL,
    familyid integer NOT NULL,
    samajid smallint NOT NULL,
    familyname character varying(120) NOT NULL,
    familynameinhindi character varying(120),
    gotra character varying(100) NOT NULL,
    addressid integer,
    region character varying(200),
    phone character varying(20),
    isphonewhatsappregistered boolean DEFAULT false NOT NULL,
    email character varying(100),
    familysearchtext character varying(500),
    active boolean DEFAULT true NOT NULL,
    familyimage character varying(120),
    imagelastupdated timestamp with time zone NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);

ALTER TABLE shared_schema.familyhistory OWNER TO postgres;

COMMENT ON TABLE shared_schema.familyhistory IS 'Table to store historical versions of family records.  
The main table always contains the latest version, while this table stores only previous versions of records.  
No entry is created here when a record is first inserted into the main table.  
For column descriptions, refer to the family table.';

COMMENT ON COLUMN shared_schema.familyhistory.historyid IS 'Unique history id to maintain history of changes';

COMMENT ON COLUMN shared_schema.familyhistory.operation IS 'Operation type i.e. UPDATE when updating main table, DELETE when deleting from main table';

CREATE SEQUENCE shared_schema.familyhistory_familyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.familyhistory_familyid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.familyhistory_familyid_seq OWNED BY shared_schema.familyhistory.familyid;

CREATE SEQUENCE shared_schema.familyhistory_historyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.familyhistory_historyid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.familyhistory_historyid_seq OWNED BY shared_schema.familyhistory.historyid;

ALTER TABLE ONLY shared_schema.familyhistory ALTER COLUMN historyid SET DEFAULT nextval('shared_schema.familyhistory_historyid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familyhistory ALTER COLUMN familyid SET DEFAULT nextval('shared_schema.familyhistory_familyid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familyhistory
    ADD CONSTRAINT familyhistory_pkey PRIMARY KEY (historyid);

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familyhistory TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familyhistory_familyid_seq TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familyhistory_historyid_seq TO familytree_app_role;
