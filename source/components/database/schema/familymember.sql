CREATE TABLE shared_schema.familymember (
    memberid integer NOT NULL,
    familyid integer NOT NULL,
    samajid smallint NOT NULL,
    logonname character varying(150),
    headoffamily boolean DEFAULT false NOT NULL,
    firstname character varying(100) NOT NULL,
    firstnameinhindi character varying(100),
    lastname character varying(100) NOT NULL,
    maidenlastname character varying(100),
    nickname character varying(100),
    nicknameinhindi character varying(100),
    gender character varying(10),
    birthday smallint,
    birthmonth smallint NOT NULL,
    birthyear smallint NOT NULL,
    maritalstatus character varying(20) NOT NULL,
    weddingdate date,
    dateofdeath date,
    phone character varying(20),
    isphoneverified boolean DEFAULT false NOT NULL,
    isphonewhatsappregistered boolean DEFAULT false NOT NULL,
    email character varying(100),
    isemailverified boolean DEFAULT false NOT NULL,
    addresssameasfamily boolean DEFAULT true NOT NULL,
    memberaddressid integer,
    educationdetails character varying(255),
    occupation character varying(255),
    hobby character varying(255),
    membersearchtext character varying(500),
    profileimage character varying(255) NOT NULL,
    profileimagethumbnail character varying(255) NOT NULL,
    imagelastupdated timestamp with time zone,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);

ALTER TABLE shared_schema.familymember OWNER TO postgres;

COMMENT ON COLUMN shared_schema.familymember.memberid IS 'A system-generated unique identifier assigned at creation. This ID is permanent and cannot be changed.';

COMMENT ON COLUMN shared_schema.familymember.familyid IS 'Family Id to which this member belongs to.';

COMMENT ON COLUMN shared_schema.familymember.logonname IS 'Logon name for this member. This will match with logon name in user table. This is optional and set when user register in application.';

COMMENT ON COLUMN shared_schema.familymember.headoffamily IS 'If true, this member is head of family. Only one member can be head of family.';

COMMENT ON COLUMN shared_schema.familymember.firstname IS 'The individual''s given name.';

COMMENT ON COLUMN shared_schema.familymember.firstnameinhindi IS 'The individual''s given name in hindi';

COMMENT ON COLUMN shared_schema.familymember.lastname IS 'The family name shared across members of the same family.';

COMMENT ON COLUMN shared_schema.familymember.maidenlastname IS 'Maiden last name before marriage. Ask user to enter maidan last name only if gender is female and maritalstatus is not single. This is going to be optional.';

COMMENT ON COLUMN shared_schema.familymember.nickname IS 'An informal or commonly used alternate name.';

COMMENT ON COLUMN shared_schema.familymember.nicknameinhindi IS 'An informal or commonly used alternate name in hindi';

COMMENT ON COLUMN shared_schema.familymember.gender IS 'Family member gender with possible value Male or Female';

COMMENT ON COLUMN shared_schema.familymember.birthday IS 'Day of the birth month value from 1 to 31';

COMMENT ON COLUMN shared_schema.familymember.birthmonth IS 'Month of the birth date, value from 1 to 12';

COMMENT ON COLUMN shared_schema.familymember.birthyear IS 'Year of the birth date, 4 digit number starting from 1900 to current year';

COMMENT ON COLUMN shared_schema.familymember.maritalstatus IS 'Member marital status with possible values Single, Married, Divorced, Widowed, Separated, Engaged';

COMMENT ON COLUMN shared_schema.familymember.weddingdate IS 'Wedding date if married. Optional field';

COMMENT ON COLUMN shared_schema.familymember.dateofdeath IS 'Date of death if member deceased. On UI show the `Is Deceased? - Yes/No`. If yes, ask for date. If day is not known, then enter 1st of Month';

COMMENT ON COLUMN shared_schema.familymember.phone IS 'Phone number in following:
      +91-912 345 6789
      +1-123 456 7890';

COMMENT ON COLUMN shared_schema.familymember.isphonewhatsappregistered IS 'True indicate, phone number is registered whatsapp number';

COMMENT ON COLUMN shared_schema.familymember.email IS 'Contact email for the family member.';

COMMENT ON COLUMN shared_schema.familymember.addresssameasfamily IS 'If value of this column is true that means member address is same as family address.';

COMMENT ON COLUMN shared_schema.familymember.memberaddressid IS 'The specific address of the member.  Address id = 0 indicate member address is same as family address';

COMMENT ON COLUMN shared_schema.familymember.educationdetails IS 'Education details. A list of academic qualifications E.g HSC; Engineering in CS; MBA';

COMMENT ON COLUMN shared_schema.familymember.occupation IS 'The member''s profession or role.  
  Examples: Software Engineer, Orthopedic Surgeon, Business Owner (flour mill), Fast Food Restaurant Owner. Also mention the place you work at.';

COMMENT ON COLUMN shared_schema.familymember.hobby IS 'Member''s hobbies or recreational interests.  
  *Example: Volleyball, cooking, painting*';

COMMENT ON COLUMN shared_schema.familymember.membersearchtext IS 'Automatically derived by the app as:  
  `[Name Hindi/english] + last name (Hindi/English) + [Region]`  
  **Example**: *Bhagwatnarayan भगवतनारायण Garothaya गरोठ्या – Amravati, MH*';

COMMENT ON COLUMN shared_schema.familymember.profileimage IS 'Path for member image in following format:
member/<memberid>/member_<memberid>.[jpg|png]
member/2231/member_2231.jpg';

COMMENT ON COLUMN shared_schema.familymember.profileimagethumbnail IS 'Path for member thumbnail image in following format:
member/<memberid>/member_thumbnail_<memberid>.[jpg|png]
member/2231/member_thumbnail_2231.jpg
This image is always derived from main image';

COMMENT ON COLUMN shared_schema.familymember.imagelastupdated IS 'Date when member image last updated';

CREATE SEQUENCE shared_schema.familymember_memberid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.familymember_memberid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.familymember_memberid_seq OWNED BY shared_schema.familymember.memberid;

ALTER TABLE ONLY shared_schema.familymember ALTER COLUMN memberid SET DEFAULT nextval('shared_schema.familymember_memberid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_email_key UNIQUE (email);

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_logonname_key UNIQUE (logonname);

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_phone_key UNIQUE (phone);

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_pkey PRIMARY KEY (memberid);

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_familyid_fkey FOREIGN KEY (familyid) REFERENCES shared_schema.family(familyid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_memberaddressid_fkey FOREIGN KEY (memberaddressid) REFERENCES shared_schema.address(addressid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_samajid_fkey FOREIGN KEY (samajid) REFERENCES shared_schema.samaj(samajid) ON UPDATE RESTRICT ON DELETE RESTRICT;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familymember TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familymember_memberid_seq TO familytree_app_role;
