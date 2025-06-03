-- Table: shared_schema.family

-- DROP TABLE IF EXISTS shared_schema.family;

CREATE TABLE IF NOT EXISTS shared_schema.family
(
    familyid SERIAL NOT NULL,
    familyname character varying(120) COLLATE pg_catalog."default" NOT NULL,
    familynameinhindi character varying(120) COLLATE pg_catalog."default",
    gotra character varying(100) COLLATE pg_catalog."default" NOT NULL,
    addressid integer,
    region character varying(200) COLLATE pg_catalog."default",
    phone character varying(20) COLLATE pg_catalog."default",
    isphonewhatsappregistered boolean NOT NULL DEFAULT false,
    email character varying(100) COLLATE pg_catalog."default",
    familydisplayname character varying(255) COLLATE pg_catalog."default",
    active boolean NOT NULL DEFAULT true,
    familyimage character varying(120) COLLATE pg_catalog."default",
	imagelastupdated timestamp with time zone NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT family_pkey PRIMARY KEY (familyid),
    CONSTRAINT family_addressid_fkey FOREIGN KEY (addressid)
        REFERENCES shared_schema.address (addressid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT VALID,
    CONSTRAINT family_createdby_fkey FOREIGN KEY (createdby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT family_lastupdatedby_fkey FOREIGN KEY (lastupdatedby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.family
    OWNER to postgres;

COMMENT ON COLUMN shared_schema.family.familyid
    IS 'System generated unique family id';

COMMENT ON COLUMN shared_schema.family.familyname
    IS 'Family name i.e. last name';

COMMENT ON COLUMN shared_schema.family.familynameinhindi
    IS 'Optional family name in hindi. This will be display in bracket name to name.';

COMMENT ON COLUMN shared_schema.family.gotra
    IS 'Gotra for this family';

COMMENT ON COLUMN shared_schema.family.addressid
    IS 'Address id for valid address';

COMMENT ON COLUMN shared_schema.family.region
    IS 'The region where the family resides. This will be system-derived based on the address using the following rules:
    - For families in **India**:  
      `Region = City + State` (e.g., *Amravati, MH*)
    - For families **abroad**:  
      `Region = Country` (e.g., *USA*)';

COMMENT ON COLUMN shared_schema.family.phone
    IS 'Phone number in following:
+91-912 345 6789
 +1-123 456 7890';

COMMENT ON COLUMN shared_schema.family.isphonewhatsappregistered
    IS 'True indicate, phone number is registered whatsapp number';

COMMENT ON COLUMN shared_schema.family.email
    IS 'Contact email for the family.';

COMMENT ON COLUMN shared_schema.family.familydisplayname
    IS 'Automatically derived by the app as:  
  `[Head of Family Name] + [Region]`  
  **Example**: *Bhagwatnarayan Garothaya – Amravati, MH*';

COMMENT ON COLUMN shared_schema.family.active
    IS 'This flag indicate, if this family active in application.';

COMMENT ON COLUMN shared_schema.family.familyimage
    IS 'Path for family image in following format:
family/<familyid>/family_<familyid>.[jpg|png]
family/231/family_231.jpg';

COMMENT ON COLUMN shared_schema.family.imagelastupdated
    IS 'Date when family image last updated';