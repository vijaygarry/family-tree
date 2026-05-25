-- Table: shared_schema.familyregistrationrequest

-- DROP TABLE IF EXISTS shared_schema.familyregistrationrequest;

CREATE TABLE IF NOT EXISTS shared_schema.familyregistrationrequest
(
    familyrequestid serial NOT NULL,
    samajid smallint NOT NULL,
    familyname character varying(120) COLLATE pg_catalog."default" NOT NULL,
    familynameinhindi character varying(120) COLLATE pg_catalog."default",
    gotra character varying(100) COLLATE pg_catalog."default",
    addressline1 character varying(255) COLLATE pg_catalog."default" NOT NULL,
    addressline2 character varying(255) COLLATE pg_catalog."default",
    addressline3 character varying(255) COLLATE pg_catalog."default",
    city character varying(120) COLLATE pg_catalog."default" NOT NULL,
    district character varying(120) COLLATE pg_catalog."default",
    state character varying(100) COLLATE pg_catalog."default" NOT NULL,
    postalcode character varying(10) COLLATE pg_catalog."default" NOT NULL,
    country character varying(100) COLLATE pg_catalog."default" DEFAULT 'India'::character varying,
    phone character varying(20) COLLATE pg_catalog."default",
    email character varying(100) COLLATE pg_catalog."default",
    createddate timestamp with time zone NOT NULL,
    clientinfo character varying(255) COLLATE pg_catalog."default",
    status character varying(100) COLLATE pg_catalog."default" NOT NULL,
    familyaddedby integer,
    dateadded timestamp with time zone,
    familyid integer,
    CONSTRAINT familyregistrationrequest_pkey PRIMARY KEY (familyrequestid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.familyregistrationrequest
    OWNER to postgres;

REVOKE ALL ON TABLE shared_schema.familyregistrationrequest FROM familytree_app_role;

GRANT DELETE, INSERT, SELECT, UPDATE ON TABLE shared_schema.familyregistrationrequest TO familytree_app_role;

GRANT ALL ON TABLE shared_schema.familyregistrationrequest TO postgres;