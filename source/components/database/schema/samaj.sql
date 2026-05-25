-- Table: shared_schema.samaj

-- DROP TABLE IF EXISTS shared_schema.samaj;

CREATE TABLE IF NOT EXISTS shared_schema.samaj
(
    samajid smallserial NOT NULL ,
    samajname character varying(255) COLLATE pg_catalog."default" NOT NULL,
    contactdetails character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT samaj_pkey PRIMARY KEY (samajid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.samaj
    OWNER to postgres;

COMMENT ON TABLE shared_schema.samaj
    IS 'Main table for samaj.';

COMMENT ON COLUMN shared_schema.samaj.samajid
    IS 'Unique samaj identifier';

COMMENT ON COLUMN shared_schema.samaj.samajname
    IS 'Unique samaj name like Rajput Chhipa';

COMMENT ON COLUMN shared_schema.samaj.contactdetails
    IS 'Main contact person for this samaj kind of super admin for samaj.';
    