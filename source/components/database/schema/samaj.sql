CREATE TABLE shared_schema.samaj (
    samajid smallint NOT NULL,
    samajname character varying(255) NOT NULL,
    contactdetails character varying(255)
);

ALTER TABLE shared_schema.samaj OWNER TO postgres;

COMMENT ON TABLE shared_schema.samaj IS 'Main table for samaj.';

COMMENT ON COLUMN shared_schema.samaj.samajid IS 'Unique samaj identifier';

COMMENT ON COLUMN shared_schema.samaj.samajname IS 'Unique samaj name like Rajput Chhipa';

COMMENT ON COLUMN shared_schema.samaj.contactdetails IS 'Main contact person for this samaj kind of super admin for samaj.';

CREATE SEQUENCE shared_schema.samaj_samajid_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.samaj_samajid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.samaj_samajid_seq OWNED BY shared_schema.samaj.samajid;

ALTER TABLE ONLY shared_schema.samaj ALTER COLUMN samajid SET DEFAULT nextval('shared_schema.samaj_samajid_seq'::regclass);

ALTER TABLE ONLY shared_schema.samaj
    ADD CONSTRAINT samaj_pkey PRIMARY KEY (samajid);

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.samaj TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.samaj_samajid_seq TO familytree_app_role;
