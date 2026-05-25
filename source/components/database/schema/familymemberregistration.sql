-- Table: shared_schema.familymemberregistration

-- DROP TABLE IF EXISTS shared_schema.familymemberregistration;

CREATE TABLE IF NOT EXISTS shared_schema.familymemberregistration
(
    memberrequestid serial NOT NULL,
    familyrequestid integer NOT NULL,
    samajid smallint NOT NULL,
    headoffamily boolean NOT NULL DEFAULT false,
    firstname character varying(100) COLLATE pg_catalog."default" NOT NULL,
    firstnameinhindi character varying(100) COLLATE pg_catalog."default",
    gender character varying(10) COLLATE pg_catalog."default",
    birthday smallint,
    birthmonth smallint NOT NULL,
    birthyear smallint NOT NULL,
    maritalstatus character varying(20) COLLATE pg_catalog."default" NOT NULL,
    weddingdate date,
    phone character varying(20) COLLATE pg_catalog."default",
    email character varying(100) COLLATE pg_catalog."default",
    addresssameasfamily boolean NOT NULL DEFAULT true,
    educationdetails character varying(255) COLLATE pg_catalog."default",
    occupation character varying(255) COLLATE pg_catalog."default",
    createddate timestamp with time zone NOT NULL,
    memberid integer,
    relationshiptype character varying(100) COLLATE pg_catalog."default" NOT NULL,
    relatedmemberid integer NOT NULL,
    CONSTRAINT familymemberregistration_pkey PRIMARY KEY (memberrequestid),
    CONSTRAINT familymemberregistration_familyrequestid_fkey FOREIGN KEY (familyrequestid)
        REFERENCES shared_schema.familyregistrationrequest (familyrequestid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.familymemberregistration
    OWNER to postgres;

REVOKE ALL ON TABLE shared_schema.familymemberregistration FROM familytree_app_role;

GRANT DELETE, INSERT, SELECT, UPDATE ON TABLE shared_schema.familymemberregistration TO familytree_app_role;

GRANT ALL ON TABLE shared_schema.familymemberregistration TO postgres;