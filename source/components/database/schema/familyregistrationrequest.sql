CREATE TABLE shared_schema.familyregistrationrequest (
    familyrequestid integer NOT NULL,
    samajid smallint NOT NULL,
    familyname character varying(120) NOT NULL,
    familynameinhindi character varying(120),
    gotra character varying(100),
    addressline1 character varying(255) NOT NULL,
    addressline2 character varying(255),
    addressline3 character varying(255),
    city character varying(120) NOT NULL,
    district character varying(120),
    state character varying(100) NOT NULL,
    postalcode character varying(10) NOT NULL,
    country character varying(100) DEFAULT 'India'::character varying,
    phone character varying(20),
    email character varying(100),
    createddate timestamp with time zone NOT NULL,
    clientinfo character varying(255),
    status character varying(100) NOT NULL,
    familyaddedby integer,
    dateadded timestamp with time zone,
    familyid integer
);

ALTER TABLE shared_schema.familyregistrationrequest OWNER TO postgres;

CREATE SEQUENCE shared_schema.familyregistrationrequest_familyrequestid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.familyregistrationrequest_familyrequestid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.familyregistrationrequest_familyrequestid_seq OWNED BY shared_schema.familyregistrationrequest.familyrequestid;

ALTER TABLE ONLY shared_schema.familyregistrationrequest ALTER COLUMN familyrequestid SET DEFAULT nextval('shared_schema.familyregistrationrequest_familyrequestid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familyregistrationrequest
    ADD CONSTRAINT familyregistrationrequest_pkey PRIMARY KEY (familyrequestid);

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familyregistrationrequest TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familyregistrationrequest_familyrequestid_seq TO familytree_app_role;
