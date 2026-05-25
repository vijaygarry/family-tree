CREATE TABLE shared_schema.familymemberregistration (
    memberrequestid integer NOT NULL,
    familyrequestid integer NOT NULL,
    samajid smallint NOT NULL,
    headoffamily boolean DEFAULT false NOT NULL,
    firstname character varying(100) NOT NULL,
    firstnameinhindi character varying(100),
    gender character varying(10),
    birthday smallint,
    birthmonth smallint NOT NULL,
    birthyear smallint NOT NULL,
    maritalstatus character varying(20) NOT NULL,
    weddingdate date,
    phone character varying(20),
    email character varying(100),
    addresssameasfamily boolean DEFAULT true NOT NULL,
    educationdetails character varying(255),
    occupation character varying(255),
    createddate timestamp with time zone NOT NULL,
    memberid integer,
    relationshiptype character varying(100) NOT NULL,
    relatedmemberid integer NOT NULL
);

ALTER TABLE shared_schema.familymemberregistration OWNER TO postgres;

CREATE SEQUENCE shared_schema.familymemberregistration_memberrequestid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.familymemberregistration_memberrequestid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.familymemberregistration_memberrequestid_seq OWNED BY shared_schema.familymemberregistration.memberrequestid;

ALTER TABLE ONLY shared_schema.familymemberregistration ALTER COLUMN memberrequestid SET DEFAULT nextval('shared_schema.familymemberregistration_memberrequestid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familymemberregistration
    ADD CONSTRAINT familymemberregistration_pkey PRIMARY KEY (memberrequestid);

ALTER TABLE ONLY shared_schema.familymemberregistration
    ADD CONSTRAINT familymemberregistration_familyrequestid_fkey FOREIGN KEY (familyrequestid) REFERENCES shared_schema.familyregistrationrequest(familyrequestid);

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familymemberregistration TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familymemberregistration_memberrequestid_seq TO familytree_app_role;
