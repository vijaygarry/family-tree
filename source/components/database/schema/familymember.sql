-- Table: shared_schema.familymember

-- DROP TABLE IF EXISTS shared_schema.familymember;

CREATE TABLE IF NOT EXISTS shared_schema.familymember
(
    memberid serial NOT NULL,
    familyid integer NOT NULL,
    headoffamily boolean NOT NULL DEFAULT false,
    firstname character varying(100) COLLATE pg_catalog."default" NOT NULL,
    lastname character varying(100) COLLATE pg_catalog."default" NOT NULL,
    nickname character varying(100) COLLATE pg_catalog."default",
    memberaddressid integer,
    phone character varying(20) COLLATE pg_catalog."default",
    isphonewhatsappregistered boolean NOT NULL DEFAULT false,
    email character varying(100) COLLATE pg_catalog."default",
    linkedinurl character varying(150) COLLATE pg_catalog."default",
    sex character varying(10) COLLATE pg_catalog."default",
    birthday smallint,
    birthmonth smallint NOT NULL,
    birthyear smallint NOT NULL,
    maritalstatus character varying(20) NOT NULL,
    educationdetails character varying(255) COLLATE pg_catalog."default",
    occupation character varying(255) COLLATE pg_catalog."default",
    workingat character varying(255) COLLATE pg_catalog."default",
    hobby character varying(255) COLLATE pg_catalog."default",
    profileimage character varying(255) NOT NULL,
    profileimagethumbnail character varying(255) NOT NULL,
    imagelastupdated timestamp with time zone,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT familymember_pkey PRIMARY KEY (memberid),
    CONSTRAINT familymember_familyid_fkey FOREIGN KEY (familyid)
        REFERENCES shared_schema.family (familyid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT familymember_memberaddressid_fkey FOREIGN KEY (memberaddressid)
        REFERENCES shared_schema.address (addressid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    FOREIGN KEY (createdby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    FOREIGN KEY (lastupdatedby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.familymember
    OWNER to postgres;

COMMENT ON COLUMN shared_schema.familymember.memberid
    IS 'A system-generated unique identifier assigned at creation. This ID is permanent and cannot be changed.';

COMMENT ON COLUMN shared_schema.familymember.familyid
    IS 'Family Id to which this member belongs to.';

COMMENT ON COLUMN shared_schema.familymember.headoffamily
    IS 'If true, this member is head of family. Only one member can be head of family.';

COMMENT ON COLUMN shared_schema.familymember.firstname
    IS 'The individual''s given name.';

COMMENT ON COLUMN shared_schema.familymember.lastname
    IS 'The family name shared across members of the same family.';

COMMENT ON COLUMN shared_schema.familymember.nickname
    IS 'An informal or commonly used alternate name.';

COMMENT ON COLUMN shared_schema.familymember.memberaddressid
    IS 'The specific address of the member.  Address id = 0 indicate member address is same as family address';

COMMENT ON COLUMN shared_schema.familymember.phone
    IS 'Phone number in following:
      +91-912 345 6789
      +1-123 456 7890';

COMMENT ON COLUMN shared_schema.familymember.isphonewhatsappregistered
    IS 'True indicate, phone number is registered whatsapp number';

COMMENT ON COLUMN shared_schema.familymember.email
    IS 'Contact email for the family member.';

COMMENT ON COLUMN shared_schema.familymember.linkedinurl
    IS 'Link to the member''s LinkedIn profile (if applicable).';

COMMENT ON COLUMN shared_schema.familymember.sex
    IS 'Family member sex with possible value Male or Female';

COMMENT ON COLUMN shared_schema.familymember.birthday
    IS 'Day of the birth month value from 1 to 31';

COMMENT ON COLUMN shared_schema.familymember.birthmonth
    IS 'Month of the birth date, value from 1 to 12';

COMMENT ON COLUMN shared_schema.familymember.birthyear
    IS 'Year of the birth date, 4 digit number starting from 1900 to current year';

COMMENT ON COLUMN shared_schema.familymember.maritalstatus
    IS 'Member marital status with possible values Single, Married, Divorced, Widowed, Separated, Engaged';

COMMENT ON COLUMN shared_schema.familymember.educationdetails
    IS 'Education details. A list of academic qualifications E.g HSC; Engineering in CS; MBA';

COMMENT ON COLUMN shared_schema.familymember.occupation
    IS 'The member''s profession or role.  
  Examples: Software Engineer, Orthopedic Surgeon, Business Owner (flour mill), Fast Food Restaurant Owner';

COMMENT ON COLUMN shared_schema.familymember.workingat
    IS 'Name of the organization, company, or place of business.';

COMMENT ON COLUMN shared_schema.familymember.hobby
    IS 'Member''s hobbies or recreational interests.  
  *Example: Volleyball, cooking, painting*';

COMMENT ON COLUMN shared_schema.familymember.profileimage
    IS 'Path for member image in following format:
member/<memberid>/member_<memberid>.[jpg|png]
member/2231/member_2231.jpg';

COMMENT ON COLUMN shared_schema.familymember.profileimagethumbnail
    IS 'Path for member thumbnail image in following format:
member/<memberid>/member_thumbnail_<memberid>.[jpg|png]
member/2231/member_thumbnail_2231.jpg
This image is always derived from main image';

COMMENT ON COLUMN shared_schema.familymember.imagelastupdated
    IS 'Date when member image last updated';