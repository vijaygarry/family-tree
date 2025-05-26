-- Table: shared_schema.address

-- DROP TABLE IF EXISTS shared_schema.address;

CREATE TABLE IF NOT EXISTS shared_schema.address
(
    addressid SERIAL NOT NULL,
    addressline1 character varying(255) COLLATE pg_catalog."default" NOT NULL,
    addressline2 character varying(255) COLLATE pg_catalog."default",
    addressline3 character varying(255) COLLATE pg_catalog."default",
    city character varying(120) COLLATE pg_catalog."default" NOT NULL,
    district character varying(120) COLLATE pg_catalog."default",
    state character varying(100) COLLATE pg_catalog."default" NOT NULL,
    postalcode character(10) COLLATE pg_catalog."default" NOT NULL,
    country character varying(100) COLLATE pg_catalog."default" DEFAULT 'India'::character varying,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT address_pkey PRIMARY KEY (addressid),
    CONSTRAINT address_createdby_fkey FOREIGN KEY (createdby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT address_lastupdatedby_fkey FOREIGN KEY (lastupdatedby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.address
    OWNER to postgres;

COMMENT ON COLUMN shared_schema.address.addressid
    IS 'Auto generated address id.';

COMMENT ON COLUMN shared_schema.address.addressline1
    IS 'Address line 1, usually house no, main street';

COMMENT ON COLUMN shared_schema.address.addressline2
    IS 'Address line 2, usually landmark, locality, etc.';

COMMENT ON COLUMN shared_schema.address.addressline3
    IS 'Address line 3, optional, extended locality, directions';

COMMENT ON COLUMN shared_schema.address.city
    IS 'City name';

COMMENT ON COLUMN shared_schema.address.district
    IS 'district name, District may contain multiple cities, towns, and villages.';

COMMENT ON COLUMN shared_schema.address.state
    IS 'State name';

COMMENT ON COLUMN shared_schema.address.postalcode
    IS 'pin code or zip code';

COMMENT ON COLUMN shared_schema.address.country
    IS 'Country name.';
    
