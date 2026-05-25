CREATE TABLE shared_schema.family (
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

ALTER TABLE shared_schema.family OWNER TO postgres;

COMMENT ON COLUMN shared_schema.family.familyid IS 'System generated unique family id';

COMMENT ON COLUMN shared_schema.family.familyname IS 'Family name i.e. last name';

COMMENT ON COLUMN shared_schema.family.familynameinhindi IS 'Optional family name in hindi. This will be display in bracket next to name.';

COMMENT ON COLUMN shared_schema.family.gotra IS 'Gotra for this family';

COMMENT ON COLUMN shared_schema.family.addressid IS 'Address id for valid address';

COMMENT ON COLUMN shared_schema.family.region IS 'The region where the family resides. This will be system-derived based on the address using the following rules:
    - For families in **India**:  
      `Region = City + State` (e.g., *Amravati, MH*)
    - For families **abroad**:  
      `Region = State + Country` (e.g., *VA, USA*)';

COMMENT ON COLUMN shared_schema.family.phone IS 'Phone number in following:
+91-912 345 6789
 +1-123 456 7890';

COMMENT ON COLUMN shared_schema.family.isphonewhatsappregistered IS 'True indicate, phone number is registered whatsapp number';

COMMENT ON COLUMN shared_schema.family.email IS 'Contact email for the family.';

COMMENT ON COLUMN shared_schema.family.familysearchtext IS 'Automatically derived by the app as:  
  `[Head of Family Name Hindi/english] + last name (Hindi/English) + [Region] + [Gotra]`  
  **Example**: *Bhagwatnarayan भगवतनारायण Garothaya गरोठ्या – Amravati, MH*';

COMMENT ON COLUMN shared_schema.family.active IS 'This flag indicate, if this family active in application.';

COMMENT ON COLUMN shared_schema.family.familyimage IS 'Path for family image in following format:
family/<familyid>/family_<familyid>.[jpg|png]
family/231/family_231.jpg';

COMMENT ON COLUMN shared_schema.family.imagelastupdated IS 'Date when family image last updated';

CREATE SEQUENCE shared_schema.family_familyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.family_familyid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.family_familyid_seq OWNED BY shared_schema.family.familyid;

ALTER TABLE ONLY shared_schema.family ALTER COLUMN familyid SET DEFAULT nextval('shared_schema.family_familyid_seq'::regclass);

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_pkey PRIMARY KEY (familyid);

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_addressid_fkey FOREIGN KEY (addressid) REFERENCES shared_schema.address(addressid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_samajid_fkey FOREIGN KEY (samajid) REFERENCES shared_schema.samaj(samajid) ON UPDATE RESTRICT ON DELETE RESTRICT;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.family TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.family_familyid_seq TO familytree_app_role;
