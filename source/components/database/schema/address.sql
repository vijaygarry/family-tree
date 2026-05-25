CREATE TABLE shared_schema.address (
    addressid integer NOT NULL,
    addressline1 character varying(255) NOT NULL,
    addressline2 character varying(255),
    addressline3 character varying(255),
    city character varying(120) NOT NULL,
    district character varying(120),
    state character varying(100) NOT NULL,
    postalcode character varying(10) NOT NULL,
    country character varying(100) DEFAULT 'India'::character varying,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);

ALTER TABLE shared_schema.address OWNER TO postgres;

COMMENT ON COLUMN shared_schema.address.addressid IS 'Auto generated address id.';

COMMENT ON COLUMN shared_schema.address.addressline1 IS 'Address line 1, usually house no, main street';

COMMENT ON COLUMN shared_schema.address.addressline2 IS 'Address line 2, usually landmark, locality, etc.';

COMMENT ON COLUMN shared_schema.address.addressline3 IS 'Address line 3, optional, extended locality, directions';

COMMENT ON COLUMN shared_schema.address.city IS 'City name';

COMMENT ON COLUMN shared_schema.address.district IS 'district name, District may contain multiple cities, towns, and villages.';

COMMENT ON COLUMN shared_schema.address.state IS 'State name';

COMMENT ON COLUMN shared_schema.address.postalcode IS 'pin code or zip code';

COMMENT ON COLUMN shared_schema.address.country IS 'Country name.';

CREATE SEQUENCE shared_schema.address_addressid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.address_addressid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.address_addressid_seq OWNED BY shared_schema.address.addressid;

ALTER TABLE ONLY shared_schema.address ALTER COLUMN addressid SET DEFAULT nextval('shared_schema.address_addressid_seq'::regclass);

ALTER TABLE ONLY shared_schema.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (addressid);

ALTER TABLE ONLY shared_schema.address
    ADD CONSTRAINT address_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.address
    ADD CONSTRAINT address_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.address TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.address_addressid_seq TO familytree_app_role;
