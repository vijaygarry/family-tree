--
-- PostgreSQL database dump
--


SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: shared_schema; Type: SCHEMA; Schema: -; Owner: familytree_master
--

CREATE SCHEMA shared_schema;


ALTER SCHEMA shared_schema OWNER TO familytree_master;

--
-- Name: SCHEMA shared_schema; Type: COMMENT; Schema: -; Owner: familytree_master
--

COMMENT ON SCHEMA shared_schema IS 'Shared schema which includes tables related to web infra.';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: address; Type: TABLE; Schema: shared_schema; Owner: postgres
--

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

--
-- Name: COLUMN address.addressid; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.address.addressid IS 'Auto generated address id.';


--
-- Name: COLUMN address.addressline1; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.address.addressline1 IS 'Address line 1, usually house no, main street';


--
-- Name: COLUMN address.addressline2; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.address.addressline2 IS 'Address line 2, usually landmark, locality, etc.';


--
-- Name: COLUMN address.addressline3; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.address.addressline3 IS 'Address line 3, optional, extended locality, directions';


--
-- Name: COLUMN address.city; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.address.city IS 'City name';


--
-- Name: COLUMN address.district; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.address.district IS 'district name, District may contain multiple cities, towns, and villages.';


--
-- Name: COLUMN address.state; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.address.state IS 'State name';


--
-- Name: COLUMN address.postalcode; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.address.postalcode IS 'pin code or zip code';


--
-- Name: COLUMN address.country; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.address.country IS 'Country name.';


--
-- Name: address_addressid_seq; Type: SEQUENCE; Schema: shared_schema; Owner: postgres
--

CREATE SEQUENCE shared_schema.address_addressid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE shared_schema.address_addressid_seq OWNER TO postgres;

--
-- Name: address_addressid_seq; Type: SEQUENCE OWNED BY; Schema: shared_schema; Owner: postgres
--

ALTER SEQUENCE shared_schema.address_addressid_seq OWNED BY shared_schema.address.addressid;


--
-- Name: appuser; Type: TABLE; Schema: shared_schema; Owner: familytree_master
--

CREATE TABLE shared_schema.appuser (
    userid integer NOT NULL,
    logonname character varying(255) NOT NULL,
    hashpassword character varying(1024) NOT NULL,
    firstname character varying(64) NOT NULL,
    lastname character varying(64) NOT NULL,
    emailid character varying(255),
    phone character varying(20),
    authenticationtype character varying(100) DEFAULT 'DB_PWD'::character varying NOT NULL,
    singlesignonid character varying(100),
    invalidloginattempts integer,
    lastlogintime timestamp with time zone,
    lastpasswordchangetime timestamp with time zone,
    status character(1) NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);


ALTER TABLE shared_schema.appuser OWNER TO familytree_master;

--
-- Name: TABLE appuser; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON TABLE shared_schema.appuser IS 'Table for application users';


--
-- Name: COLUMN appuser.userid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.userid IS 'Unique numeric id for user. This value will be used by other tables for reference.';


--
-- Name: COLUMN appuser.logonname; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.logonname IS 'Unique logon name to identify the user. This can be alphanumaric id or email id';


--
-- Name: COLUMN appuser.hashpassword; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.hashpassword IS 'Encrypted (one way hash) password for this user';


--
-- Name: COLUMN appuser.firstname; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.firstname IS 'Users first name';


--
-- Name: COLUMN appuser.lastname; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.lastname IS 'Users last name';


--
-- Name: COLUMN appuser.emailid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.emailid IS 'Unique User emailid';


--
-- Name: COLUMN appuser.authenticationtype; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.authenticationtype IS 'How this user is authenticated in application. Options are DB_PWD, LDAP, OAUTH';


--
-- Name: COLUMN appuser.singlesignonid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.singlesignonid IS 'Is used to authenticate user with single sign on application like Free IPA or Active Directory. In most case, this will be same as logonname';


--
-- Name: COLUMN appuser.invalidloginattempts; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.invalidloginattempts IS 'This columns maintain number of invalid attempts for this user.
On every successful login this number get reset to zero.
If this number goes above threshold invalid login attempt, user status will get updtaed to locked.';


--
-- Name: COLUMN appuser.lastlogintime; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.lastlogintime IS 'Time when this user is successfully login to application';


--
-- Name: COLUMN appuser.status; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.status IS 'Status of the user.
A - Active
I - Inactive
L - Lock
Only active user can login and perform transactions.';


--
-- Name: COLUMN appuser.createdby; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.createdby IS 'User id for user who created this record';


--
-- Name: COLUMN appuser.createddate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.createddate IS 'Time when this record was created';


--
-- Name: COLUMN appuser.lastupdatedby; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.lastupdatedby IS 'User id for user who last updated this record';


--
-- Name: COLUMN appuser.lastupdateddate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.appuser.lastupdateddate IS 'Time when this record was last updated';


--
-- Name: appuser_userid_seq; Type: SEQUENCE; Schema: shared_schema; Owner: familytree_master
--

CREATE SEQUENCE shared_schema.appuser_userid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE shared_schema.appuser_userid_seq OWNER TO familytree_master;

--
-- Name: appuser_userid_seq; Type: SEQUENCE OWNED BY; Schema: shared_schema; Owner: familytree_master
--

ALTER SEQUENCE shared_schema.appuser_userid_seq OWNED BY shared_schema.appuser.userid;


--
-- Name: family; Type: TABLE; Schema: shared_schema; Owner: postgres
--

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

--
-- Name: COLUMN family.familyid; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.familyid IS 'System generated unique family id';


--
-- Name: COLUMN family.familyname; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.familyname IS 'Family name i.e. last name';


--
-- Name: COLUMN family.familynameinhindi; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.familynameinhindi IS 'Optional family name in hindi. This will be display in bracket next to name.';


--
-- Name: COLUMN family.gotra; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.gotra IS 'Gotra for this family';


--
-- Name: COLUMN family.addressid; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.addressid IS 'Address id for valid address';


--
-- Name: COLUMN family.region; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.region IS 'The region where the family resides. This will be system-derived based on the address using the following rules:
    - For families in **India**:  
      `Region = City + State` (e.g., *Amravati, MH*)
    - For families **abroad**:  
      `Region = State + Country` (e.g., *VA, USA*)';


--
-- Name: COLUMN family.phone; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.phone IS 'Phone number in following:
+91-912 345 6789
 +1-123 456 7890';


--
-- Name: COLUMN family.isphonewhatsappregistered; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.isphonewhatsappregistered IS 'True indicate, phone number is registered whatsapp number';


--
-- Name: COLUMN family.email; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.email IS 'Contact email for the family.';


--
-- Name: COLUMN family.familysearchtext; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.familysearchtext IS 'Automatically derived by the app as:  
  `[Head of Family Name Hindi/english] + last name (Hindi/English) + [Region] + [Gotra]`  
  **Example**: *Bhagwatnarayan भगवतनारायण Garothaya गरोठ्या – Amravati, MH*';


--
-- Name: COLUMN family.active; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.active IS 'This flag indicate, if this family active in application.';


--
-- Name: COLUMN family.familyimage; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.familyimage IS 'Path for family image in following format:
family/<familyid>/family_<familyid>.[jpg|png]
family/231/family_231.jpg';


--
-- Name: COLUMN family.imagelastupdated; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.family.imagelastupdated IS 'Date when family image last updated';


--
-- Name: family_familyid_seq; Type: SEQUENCE; Schema: shared_schema; Owner: postgres
--

CREATE SEQUENCE shared_schema.family_familyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE shared_schema.family_familyid_seq OWNER TO postgres;

--
-- Name: family_familyid_seq; Type: SEQUENCE OWNED BY; Schema: shared_schema; Owner: postgres
--

ALTER SEQUENCE shared_schema.family_familyid_seq OWNED BY shared_schema.family.familyid;


--
-- Name: familyhistory; Type: TABLE; Schema: shared_schema; Owner: postgres
--

CREATE TABLE shared_schema.familyhistory (
    historyid integer NOT NULL,
    operation character varying(10) NOT NULL,
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


ALTER TABLE shared_schema.familyhistory OWNER TO postgres;

--
-- Name: TABLE familyhistory; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON TABLE shared_schema.familyhistory IS 'Table to store historical versions of family records.  
The main table always contains the latest version, while this table stores only previous versions of records.  
No entry is created here when a record is first inserted into the main table.  
For column descriptions, refer to the family table.';


--
-- Name: COLUMN familyhistory.historyid; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familyhistory.historyid IS 'Unique history id to maintain history of changes';


--
-- Name: COLUMN familyhistory.operation; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familyhistory.operation IS 'Operation type i.e. UPDATE when updating main table, DELETE when deleting from main table';


--
-- Name: familyhistory_familyid_seq; Type: SEQUENCE; Schema: shared_schema; Owner: postgres
--

CREATE SEQUENCE shared_schema.familyhistory_familyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE shared_schema.familyhistory_familyid_seq OWNER TO postgres;

--
-- Name: familyhistory_familyid_seq; Type: SEQUENCE OWNED BY; Schema: shared_schema; Owner: postgres
--

ALTER SEQUENCE shared_schema.familyhistory_familyid_seq OWNED BY shared_schema.familyhistory.familyid;


--
-- Name: familyhistory_historyid_seq; Type: SEQUENCE; Schema: shared_schema; Owner: postgres
--

CREATE SEQUENCE shared_schema.familyhistory_historyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE shared_schema.familyhistory_historyid_seq OWNER TO postgres;

--
-- Name: familyhistory_historyid_seq; Type: SEQUENCE OWNED BY; Schema: shared_schema; Owner: postgres
--

ALTER SEQUENCE shared_schema.familyhistory_historyid_seq OWNED BY shared_schema.familyhistory.historyid;


--
-- Name: familymember; Type: TABLE; Schema: shared_schema; Owner: postgres
--

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

--
-- Name: COLUMN familymember.memberid; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.memberid IS 'A system-generated unique identifier assigned at creation. This ID is permanent and cannot be changed.';


--
-- Name: COLUMN familymember.familyid; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.familyid IS 'Family Id to which this member belongs to.';


--
-- Name: COLUMN familymember.logonname; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.logonname IS 'Logon name for this member. This will match with logon name in user table. This is optional and set when user register in application.';


--
-- Name: COLUMN familymember.headoffamily; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.headoffamily IS 'If true, this member is head of family. Only one member can be head of family.';


--
-- Name: COLUMN familymember.firstname; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.firstname IS 'The individual''s given name.';


--
-- Name: COLUMN familymember.firstnameinhindi; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.firstnameinhindi IS 'The individual''s given name in hindi';


--
-- Name: COLUMN familymember.lastname; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.lastname IS 'The family name shared across members of the same family.';


--
-- Name: COLUMN familymember.maidenlastname; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.maidenlastname IS 'Maiden last name before marriage. Ask user to enter maidan last name only if gender is female and maritalstatus is not single. This is going to be optional.';


--
-- Name: COLUMN familymember.nickname; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.nickname IS 'An informal or commonly used alternate name.';


--
-- Name: COLUMN familymember.nicknameinhindi; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.nicknameinhindi IS 'An informal or commonly used alternate name in hindi';


--
-- Name: COLUMN familymember.gender; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.gender IS 'Family member gender with possible value Male or Female';


--
-- Name: COLUMN familymember.birthday; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.birthday IS 'Day of the birth month value from 1 to 31';


--
-- Name: COLUMN familymember.birthmonth; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.birthmonth IS 'Month of the birth date, value from 1 to 12';


--
-- Name: COLUMN familymember.birthyear; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.birthyear IS 'Year of the birth date, 4 digit number starting from 1900 to current year';


--
-- Name: COLUMN familymember.maritalstatus; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.maritalstatus IS 'Member marital status with possible values Single, Married, Divorced, Widowed, Separated, Engaged';


--
-- Name: COLUMN familymember.weddingdate; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.weddingdate IS 'Wedding date if married. Optional field';


--
-- Name: COLUMN familymember.dateofdeath; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.dateofdeath IS 'Date of death if member deceased. On UI show the `Is Deceased? - Yes/No`. If yes, ask for date. If day is not known, then enter 1st of Month';


--
-- Name: COLUMN familymember.phone; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.phone IS 'Phone number in following:
      +91-912 345 6789
      +1-123 456 7890';


--
-- Name: COLUMN familymember.isphonewhatsappregistered; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.isphonewhatsappregistered IS 'True indicate, phone number is registered whatsapp number';


--
-- Name: COLUMN familymember.email; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.email IS 'Contact email for the family member.';


--
-- Name: COLUMN familymember.addresssameasfamily; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.addresssameasfamily IS 'If value of this column is true that means member address is same as family address.';


--
-- Name: COLUMN familymember.memberaddressid; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.memberaddressid IS 'The specific address of the member.  Address id = 0 indicate member address is same as family address';


--
-- Name: COLUMN familymember.educationdetails; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.educationdetails IS 'Education details. A list of academic qualifications E.g HSC; Engineering in CS; MBA';


--
-- Name: COLUMN familymember.occupation; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.occupation IS 'The member''s profession or role.  
  Examples: Software Engineer, Orthopedic Surgeon, Business Owner (flour mill), Fast Food Restaurant Owner. Also mention the place you work at.';


--
-- Name: COLUMN familymember.hobby; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.hobby IS 'Member''s hobbies or recreational interests.  
  *Example: Volleyball, cooking, painting*';


--
-- Name: COLUMN familymember.membersearchtext; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.membersearchtext IS 'Automatically derived by the app as:  
  `[Name Hindi/english] + last name (Hindi/English) + [Region]`  
  **Example**: *Bhagwatnarayan भगवतनारायण Garothaya गरोठ्या – Amravati, MH*';


--
-- Name: COLUMN familymember.profileimage; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.profileimage IS 'Path for member image in following format:
member/<memberid>/member_<memberid>.[jpg|png]
member/2231/member_2231.jpg';


--
-- Name: COLUMN familymember.profileimagethumbnail; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.profileimagethumbnail IS 'Path for member thumbnail image in following format:
member/<memberid>/member_thumbnail_<memberid>.[jpg|png]
member/2231/member_thumbnail_2231.jpg
This image is always derived from main image';


--
-- Name: COLUMN familymember.imagelastupdated; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymember.imagelastupdated IS 'Date when member image last updated';


--
-- Name: familymember_memberid_seq; Type: SEQUENCE; Schema: shared_schema; Owner: postgres
--

CREATE SEQUENCE shared_schema.familymember_memberid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE shared_schema.familymember_memberid_seq OWNER TO postgres;

--
-- Name: familymember_memberid_seq; Type: SEQUENCE OWNED BY; Schema: shared_schema; Owner: postgres
--

ALTER SEQUENCE shared_schema.familymember_memberid_seq OWNED BY shared_schema.familymember.memberid;


--
-- Name: familymemberhistory; Type: TABLE; Schema: shared_schema; Owner: postgres
--

CREATE TABLE shared_schema.familymemberhistory (
    historyid integer NOT NULL,
    operation character varying(10) NOT NULL,
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


ALTER TABLE shared_schema.familymemberhistory OWNER TO postgres;

--
-- Name: TABLE familymemberhistory; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON TABLE shared_schema.familymemberhistory IS 'Table to store historical versions of family member profile records.  
The main table always contains the latest version, while this table stores only previous versions of records.  
No entry is created here when a record is first inserted into the main table.  
For column descriptions, refer to the familymember table.';


--
-- Name: COLUMN familymemberhistory.historyid; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymemberhistory.historyid IS 'Unique history id to maintain history of changes';


--
-- Name: COLUMN familymemberhistory.operation; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.familymemberhistory.operation IS 'Operation type i.e. UPDATE when updating main table, DELETE when deleting from main table';


--
-- Name: familymemberhistory_historyid_seq; Type: SEQUENCE; Schema: shared_schema; Owner: postgres
--

CREATE SEQUENCE shared_schema.familymemberhistory_historyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE shared_schema.familymemberhistory_historyid_seq OWNER TO postgres;

--
-- Name: familymemberhistory_historyid_seq; Type: SEQUENCE OWNED BY; Schema: shared_schema; Owner: postgres
--

ALTER SEQUENCE shared_schema.familymemberhistory_historyid_seq OWNED BY shared_schema.familymemberhistory.historyid;


--
-- Name: lkpconfig; Type: TABLE; Schema: shared_schema; Owner: familytree_master
--

CREATE TABLE shared_schema.lkpconfig (
    configname character varying(255) NOT NULL,
    paramname character varying(255) NOT NULL,
    paramvalue character varying(500) NOT NULL,
    enable boolean DEFAULT true NOT NULL,
    listorderseq smallint,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);


ALTER TABLE shared_schema.lkpconfig OWNER TO familytree_master;

--
-- Name: COLUMN lkpconfig.configname; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpconfig.configname IS 'Main config name e.g. COUNTRYNAME';


--
-- Name: COLUMN lkpconfig.paramname; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpconfig.paramname IS 'config parameter name e.g. HINDI. This will be the same as config name if no sub config name';


--
-- Name: COLUMN lkpconfig.paramvalue; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpconfig.paramvalue IS 'Config value for config name and config param name';


--
-- Name: COLUMN lkpconfig.enable; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpconfig.enable IS 'Y means enabled and should be pickup by application';


--
-- Name: COLUMN lkpconfig.listorderseq; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpconfig.listorderseq IS 'If config has multiple param, then this will be order of config.';


--
-- Name: COLUMN lkpconfig.createdby; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpconfig.createdby IS 'User id for user who created this record';


--
-- Name: COLUMN lkpconfig.createddate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpconfig.createddate IS 'Time when this record was created';


--
-- Name: COLUMN lkpconfig.lastupdatedby; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpconfig.lastupdatedby IS 'User id for user who last updated this record';


--
-- Name: COLUMN lkpconfig.lastupdateddate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpconfig.lastupdateddate IS 'Time when this record was last updated';


--
-- Name: lkpoperation; Type: TABLE; Schema: shared_schema; Owner: familytree_master
--

CREATE TABLE shared_schema.lkpoperation (
    operationid character varying(55) NOT NULL,
    description character varying(255) NOT NULL,
    beanname character varying(255) NOT NULL,
    isauthorizationrequired boolean DEFAULT true NOT NULL,
    isauditrequired boolean DEFAULT true NOT NULL,
    authorizationtype character varying(100) DEFAULT 'ROLE_BASE'::character varying NOT NULL,
    active boolean DEFAULT true NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);


ALTER TABLE shared_schema.lkpoperation OWNER TO familytree_master;

--
-- Name: TABLE lkpoperation; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON TABLE shared_schema.lkpoperation IS 'Main operation table which mapped to one API call to application.';


--
-- Name: COLUMN lkpoperation.operationid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpoperation.operationid IS 'Unique alpha numeric opearation id';


--
-- Name: COLUMN lkpoperation.description; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpoperation.description IS 'Operation description';


--
-- Name: COLUMN lkpoperation.beanname; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpoperation.beanname IS 'Bean name/class name to invoke for this operation';


--
-- Name: COLUMN lkpoperation.isauthorizationrequired; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpoperation.isauthorizationrequired IS 'True indicate user authentication/authorization is required to execute this operation.';


--
-- Name: COLUMN lkpoperation.isauditrequired; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpoperation.isauditrequired IS 'True indicate audit this transaction';


--
-- Name: COLUMN lkpoperation.authorizationtype; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpoperation.authorizationtype IS 'Type of authorization required for this operation. Possible values are NO_AUTHORIZATION, IP_BASE, ROLE_BASE';


--
-- Name: COLUMN lkpoperation.active; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpoperation.active IS 'If this value is true, then application will load this operation, if this flag is false, then application won''t recognize this operation.';


--
-- Name: COLUMN lkpoperation.createdby; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpoperation.createdby IS 'User id for user who created this record';


--
-- Name: COLUMN lkpoperation.createddate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpoperation.createddate IS 'Time when this record was created';


--
-- Name: COLUMN lkpoperation.lastupdatedby; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpoperation.lastupdatedby IS 'User id for user who last updated this record';


--
-- Name: COLUMN lkpoperation.lastupdateddate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkpoperation.lastupdateddate IS 'Time when this record was last updated';


--
-- Name: lkprole; Type: TABLE; Schema: shared_schema; Owner: familytree_master
--

CREATE TABLE shared_schema.lkprole (
    roleid character varying(55) NOT NULL,
    roledesc character varying(255) NOT NULL,
    enable boolean DEFAULT true NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);


ALTER TABLE shared_schema.lkprole OWNER TO familytree_master;

--
-- Name: TABLE lkprole; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON TABLE shared_schema.lkprole IS 'Role table';


--
-- Name: COLUMN lkprole.roleid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkprole.roleid IS 'Unique alphanumeric role id';


--
-- Name: COLUMN lkprole.roledesc; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkprole.roledesc IS 'Desc for this role';


--
-- Name: COLUMN lkprole.enable; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkprole.enable IS 'This flag indicate if this role is active.';


--
-- Name: lkproleoperationmap; Type: TABLE; Schema: shared_schema; Owner: familytree_master
--

CREATE TABLE shared_schema.lkproleoperationmap (
    roleid character varying(55) NOT NULL,
    operationid character varying(55) NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);


ALTER TABLE shared_schema.lkproleoperationmap OWNER TO familytree_master;

--
-- Name: TABLE lkproleoperationmap; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON TABLE shared_schema.lkproleoperationmap IS 'Role Operation link table. This will have list of operation mapped to a role.';


--
-- Name: COLUMN lkproleoperationmap.roleid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkproleoperationmap.roleid IS 'Role Id';


--
-- Name: COLUMN lkproleoperationmap.operationid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.lkproleoperationmap.operationid IS 'Operation id assign to role';


--
-- Name: memberrelationship; Type: TABLE; Schema: shared_schema; Owner: postgres
--

CREATE TABLE shared_schema.memberrelationship (
    memberid integer NOT NULL,
    relationshiptype character varying(55) NOT NULL,
    relatedmemberid integer NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);


ALTER TABLE shared_schema.memberrelationship OWNER TO postgres;

--
-- Name: TABLE memberrelationship; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON TABLE shared_schema.memberrelationship IS 'Table to maintain relationship.
Read the record as 
memberid''s relationshiptype is relatedmemberid
E.g: memberid = Bhagwatnarayan, relationshiptype = Son and relatedmemberid = Vijay
then read this record as 
Bhagwatnarayan''s Son is Vijay.';


--
-- Name: COLUMN memberrelationship.memberid; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.memberrelationship.memberid IS 'The person whose relationship is being defined. memberid''s relationshiptype is relatedmemberid';


--
-- Name: COLUMN memberrelationship.relationshiptype; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.memberrelationship.relationshiptype IS 'Relationship type for this member. Possible options Son, Daughter, Husband, Wife';


--
-- Name: COLUMN memberrelationship.relatedmemberid; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.memberrelationship.relatedmemberid IS 'The other person in the relationship. relatedmemberid is relationshiptype of memberid';


--
-- Name: mstuserrolemap; Type: TABLE; Schema: shared_schema; Owner: familytree_master
--

CREATE TABLE shared_schema.mstuserrolemap (
    userid integer NOT NULL,
    roleid character varying(100) NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);


ALTER TABLE shared_schema.mstuserrolemap OWNER TO familytree_master;

--
-- Name: TABLE mstuserrolemap; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON TABLE shared_schema.mstuserrolemap IS 'User role link table. This table will have list of roles assign to user.';


--
-- Name: COLUMN mstuserrolemap.userid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.mstuserrolemap.userid IS 'User Id';


--
-- Name: COLUMN mstuserrolemap.roleid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.mstuserrolemap.roleid IS 'Role Id';


--
-- Name: otpverification; Type: TABLE; Schema: shared_schema; Owner: familytree_master
--

CREATE TABLE shared_schema.otpverification (
    emailid character varying(255) NOT NULL,
    otptype character varying(50) NOT NULL,
    hashotpcode character varying(1024) NOT NULL,
    requestid character varying(55) NOT NULL,
    status character varying(20) DEFAULT 'Pending'::character varying NOT NULL,
    verifiedat timestamp with time zone,
    expirydate timestamp with time zone NOT NULL,
    attempts integer DEFAULT 0 NOT NULL,
    lastattemptdate timestamp with time zone,
    createddate timestamp with time zone NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);


ALTER TABLE shared_schema.otpverification OWNER TO familytree_master;

--
-- Name: TABLE otpverification; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON TABLE shared_schema.otpverification IS 'Table for storing OTP verification details';


--
-- Name: COLUMN otpverification.emailid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverification.emailid IS 'Email address associated with the OTP';


--
-- Name: COLUMN otpverification.otptype; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverification.otptype IS 'Type of OTP (e.g., SignUp, ForgotPassword)';


--
-- Name: COLUMN otpverification.hashotpcode; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverification.hashotpcode IS 'Encrypted (one way hash) OTP code for this user';


--
-- Name: COLUMN otpverification.requestid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverification.requestid IS 'Identifier for the OTP request';


--
-- Name: COLUMN otpverification.status; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverification.status IS 'Current status of the OTP (e.g., Pending, Verified, Expired)';


--
-- Name: COLUMN otpverification.verifiedat; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverification.verifiedat IS 'Timestamp when the OTP was verified';


--
-- Name: COLUMN otpverification.expirydate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverification.expirydate IS 'Timestamp when the OTP expires';


--
-- Name: COLUMN otpverification.attempts; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverification.attempts IS 'Number of attempts made to verify the OTP';


--
-- Name: COLUMN otpverification.lastattemptdate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverification.lastattemptdate IS 'Timestamp of the last attempt to verify the OTP';


--
-- Name: COLUMN otpverification.createddate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverification.createddate IS 'Timestamp when the OTP verification entry was created';


--
-- Name: COLUMN otpverification.lastupdateddate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverification.lastupdateddate IS 'Timestamp when the OTP verification entry was last updated';


--
-- Name: otpverificationhistory; Type: TABLE; Schema: shared_schema; Owner: familytree_master
--

CREATE TABLE shared_schema.otpverificationhistory (
    seqid bigint NOT NULL,
    emailid character varying(255) NOT NULL,
    otptype character varying(50) NOT NULL,
    hashotpcode character varying(1024) NOT NULL,
    requestid character varying(55) NOT NULL,
    status character varying(20) DEFAULT 'Pending'::character varying NOT NULL,
    verifiedat timestamp with time zone,
    expirydate timestamp with time zone NOT NULL,
    attempts integer DEFAULT 0 NOT NULL,
    lastattemptdate timestamp with time zone,
    createddate timestamp with time zone NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);


ALTER TABLE shared_schema.otpverificationhistory OWNER TO familytree_master;

--
-- Name: TABLE otpverificationhistory; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON TABLE shared_schema.otpverificationhistory IS 'Table for storing OTP verification details';


--
-- Name: COLUMN otpverificationhistory.seqid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.seqid IS 'Unique identifier for each OTP verification entry';


--
-- Name: COLUMN otpverificationhistory.emailid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.emailid IS 'Email address associated with the OTP';


--
-- Name: COLUMN otpverificationhistory.otptype; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.otptype IS 'Type of OTP (e.g., SignUp, ForgotPassword)';


--
-- Name: COLUMN otpverificationhistory.hashotpcode; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.hashotpcode IS 'Encrypted (one way hash) OTP code for this user';


--
-- Name: COLUMN otpverificationhistory.requestid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.requestid IS 'Identifier for the OTP request';


--
-- Name: COLUMN otpverificationhistory.status; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.status IS 'Current status of the OTP (e.g., Pending, Verified, Expired)';


--
-- Name: COLUMN otpverificationhistory.verifiedat; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.verifiedat IS 'Timestamp when the OTP was verified';


--
-- Name: COLUMN otpverificationhistory.expirydate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.expirydate IS 'Timestamp when the OTP expires';


--
-- Name: COLUMN otpverificationhistory.attempts; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.attempts IS 'Number of attempts made to verify the OTP';


--
-- Name: COLUMN otpverificationhistory.lastattemptdate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.lastattemptdate IS 'Timestamp of the last attempt to verify the OTP';


--
-- Name: COLUMN otpverificationhistory.createddate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.createddate IS 'Timestamp when the OTP verification entry was created';


--
-- Name: COLUMN otpverificationhistory.lastupdateddate; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.otpverificationhistory.lastupdateddate IS 'Timestamp when the OTP verification entry was last updated';


--
-- Name: otpverificationhistory_seqid_seq; Type: SEQUENCE; Schema: shared_schema; Owner: familytree_master
--

CREATE SEQUENCE shared_schema.otpverificationhistory_seqid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE shared_schema.otpverificationhistory_seqid_seq OWNER TO familytree_master;

--
-- Name: otpverificationhistory_seqid_seq; Type: SEQUENCE OWNED BY; Schema: shared_schema; Owner: familytree_master
--

ALTER SEQUENCE shared_schema.otpverificationhistory_seqid_seq OWNED BY shared_schema.otpverificationhistory.seqid;


--
-- Name: samaj; Type: TABLE; Schema: shared_schema; Owner: postgres
--

CREATE TABLE shared_schema.samaj (
    samajid smallint NOT NULL,
    samajname character varying(255) NOT NULL,
    contactdetails character varying(255)
);


ALTER TABLE shared_schema.samaj OWNER TO postgres;

--
-- Name: TABLE samaj; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON TABLE shared_schema.samaj IS 'Main table for samaj.';


--
-- Name: COLUMN samaj.samajid; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.samaj.samajid IS 'Unique samaj identifier';


--
-- Name: COLUMN samaj.samajname; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.samaj.samajname IS 'Unique samaj name like Rajput Chhipa';


--
-- Name: COLUMN samaj.contactdetails; Type: COMMENT; Schema: shared_schema; Owner: postgres
--

COMMENT ON COLUMN shared_schema.samaj.contactdetails IS 'Main contact person for this samaj kind of super admin for samaj.';


--
-- Name: samaj_samajid_seq; Type: SEQUENCE; Schema: shared_schema; Owner: postgres
--

CREATE SEQUENCE shared_schema.samaj_samajid_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE shared_schema.samaj_samajid_seq OWNER TO postgres;

--
-- Name: samaj_samajid_seq; Type: SEQUENCE OWNED BY; Schema: shared_schema; Owner: postgres
--

ALTER SEQUENCE shared_schema.samaj_samajid_seq OWNED BY shared_schema.samaj.samajid;


--
-- Name: txtsession; Type: TABLE; Schema: shared_schema; Owner: familytree_master
--

CREATE TABLE shared_schema.txtsession (
    sessionid bigint NOT NULL,
    userid integer,
    channelid character varying(64) NOT NULL,
    active boolean DEFAULT true NOT NULL,
    authenticated boolean DEFAULT false NOT NULL,
    sessioncreationtime timestamp with time zone NOT NULL,
    logouttime timestamp with time zone,
    lastaccesstime timestamp with time zone,
    exitcode smallint DEFAULT 1,
    apphostname character varying(80) NOT NULL,
    clientipaddress character varying(55) NOT NULL,
    user_agent text
);


ALTER TABLE shared_schema.txtsession OWNER TO familytree_master;

--
-- Name: TABLE txtsession; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON TABLE shared_schema.txtsession IS 'Maintain session created in application';


--
-- Name: COLUMN txtsession.sessionid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.sessionid IS 'Unique session id.';


--
-- Name: COLUMN txtsession.userid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.userid IS 'User this session belongs to';


--
-- Name: COLUMN txtsession.channelid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.channelid IS 'Channel this session is created for. E.g. Server, Browser, Mobile';


--
-- Name: COLUMN txtsession.active; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.active IS 'Flag indicate if this is active session.';


--
-- Name: COLUMN txtsession.authenticated; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.authenticated IS 'Flag indicate if this is authenticated session.';


--
-- Name: COLUMN txtsession.sessioncreationtime; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.sessioncreationtime IS 'Session creation time';


--
-- Name: COLUMN txtsession.logouttime; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.logouttime IS 'Session logout time';


--
-- Name: COLUMN txtsession.lastaccesstime; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.lastaccesstime IS 'Last access session time to see if session should be kept active.';


--
-- Name: COLUMN txtsession.exitcode; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.exitcode IS 'How the session is terminated. Possible values 1-> User Logout, 2-> Session Timeout, 3-> Override by new session, 4-> Kill by Admin';


--
-- Name: COLUMN txtsession.apphostname; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.apphostname IS 'application Hostname or IP of Application from where this session is created';


--
-- Name: COLUMN txtsession.clientipaddress; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.clientipaddress IS 'Client IP address';


--
-- Name: COLUMN txtsession.user_agent; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsession.user_agent IS 'Client user agent including browser name and version, client OS name and version. Obtain from user-agent header of HTTP request. This may not be accurate.';


--
-- Name: txtsession_sessionid_seq; Type: SEQUENCE; Schema: shared_schema; Owner: familytree_master
--

CREATE SEQUENCE shared_schema.txtsession_sessionid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE shared_schema.txtsession_sessionid_seq OWNER TO familytree_master;

--
-- Name: txtsession_sessionid_seq; Type: SEQUENCE OWNED BY; Schema: shared_schema; Owner: familytree_master
--

ALTER SEQUENCE shared_schema.txtsession_sessionid_seq OWNED BY shared_schema.txtsession.sessionid;


--
-- Name: txtsessiontxn; Type: TABLE; Schema: shared_schema; Owner: familytree_master
--

CREATE TABLE shared_schema.txtsessiontxn (
    txnid bigint NOT NULL,
    sessionid bigint NOT NULL,
    operationid character varying(55) NOT NULL,
    userid integer,
    txnstarttime timestamp with time zone NOT NULL,
    txnlatencymillis bigint,
    httpresponsecode integer,
    request text,
    response text
);


ALTER TABLE shared_schema.txtsessiontxn OWNER TO familytree_master;

--
-- Name: TABLE txtsessiontxn; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON TABLE shared_schema.txtsessiontxn IS 'Table holds list of transactions perform in session';


--
-- Name: COLUMN txtsessiontxn.txnid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsessiontxn.txnid IS 'Unique transaction id.';


--
-- Name: COLUMN txtsessiontxn.sessionid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsessiontxn.sessionid IS 'Session id.';


--
-- Name: COLUMN txtsessiontxn.operationid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsessiontxn.operationid IS 'Operation id';


--
-- Name: COLUMN txtsessiontxn.userid; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsessiontxn.userid IS 'User who performaing this operation';


--
-- Name: COLUMN txtsessiontxn.txnstarttime; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsessiontxn.txnstarttime IS 'Transaction start time';


--
-- Name: COLUMN txtsessiontxn.txnlatencymillis; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsessiontxn.txnlatencymillis IS 'Time took to process request in millis';


--
-- Name: COLUMN txtsessiontxn.httpresponsecode; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsessiontxn.httpresponsecode IS 'HTTP response code for this operation';


--
-- Name: COLUMN txtsessiontxn.request; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsessiontxn.request IS 'Request object. This can be complete json object';


--
-- Name: COLUMN txtsessiontxn.response; Type: COMMENT; Schema: shared_schema; Owner: familytree_master
--

COMMENT ON COLUMN shared_schema.txtsessiontxn.response IS 'Response object. This can be complete json object';


--
-- Name: txtsessiontxn_txnid_seq; Type: SEQUENCE; Schema: shared_schema; Owner: familytree_master
--

CREATE SEQUENCE shared_schema.txtsessiontxn_txnid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE shared_schema.txtsessiontxn_txnid_seq OWNER TO familytree_master;

--
-- Name: txtsessiontxn_txnid_seq; Type: SEQUENCE OWNED BY; Schema: shared_schema; Owner: familytree_master
--

ALTER SEQUENCE shared_schema.txtsessiontxn_txnid_seq OWNED BY shared_schema.txtsessiontxn.txnid;


--
-- Name: address addressid; Type: DEFAULT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.address ALTER COLUMN addressid SET DEFAULT nextval('shared_schema.address_addressid_seq'::regclass);


--
-- Name: appuser userid; Type: DEFAULT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.appuser ALTER COLUMN userid SET DEFAULT nextval('shared_schema.appuser_userid_seq'::regclass);


--
-- Name: family familyid; Type: DEFAULT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.family ALTER COLUMN familyid SET DEFAULT nextval('shared_schema.family_familyid_seq'::regclass);


--
-- Name: familyhistory historyid; Type: DEFAULT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familyhistory ALTER COLUMN historyid SET DEFAULT nextval('shared_schema.familyhistory_historyid_seq'::regclass);


--
-- Name: familyhistory familyid; Type: DEFAULT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familyhistory ALTER COLUMN familyid SET DEFAULT nextval('shared_schema.familyhistory_familyid_seq'::regclass);


--
-- Name: familymember memberid; Type: DEFAULT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymember ALTER COLUMN memberid SET DEFAULT nextval('shared_schema.familymember_memberid_seq'::regclass);


--
-- Name: familymemberhistory historyid; Type: DEFAULT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymemberhistory ALTER COLUMN historyid SET DEFAULT nextval('shared_schema.familymemberhistory_historyid_seq'::regclass);


--
-- Name: otpverificationhistory seqid; Type: DEFAULT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.otpverificationhistory ALTER COLUMN seqid SET DEFAULT nextval('shared_schema.otpverificationhistory_seqid_seq'::regclass);


--
-- Name: samaj samajid; Type: DEFAULT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.samaj ALTER COLUMN samajid SET DEFAULT nextval('shared_schema.samaj_samajid_seq'::regclass);


--
-- Name: txtsession sessionid; Type: DEFAULT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.txtsession ALTER COLUMN sessionid SET DEFAULT nextval('shared_schema.txtsession_sessionid_seq'::regclass);


--
-- Name: txtsessiontxn txnid; Type: DEFAULT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.txtsessiontxn ALTER COLUMN txnid SET DEFAULT nextval('shared_schema.txtsessiontxn_txnid_seq'::regclass);


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (addressid);


--
-- Name: appuser appuser_emailid_key; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.appuser
    ADD CONSTRAINT appuser_emailid_key UNIQUE (emailid);


--
-- Name: appuser appuser_logonname_key; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.appuser
    ADD CONSTRAINT appuser_logonname_key UNIQUE (logonname);


--
-- Name: appuser appuser_phone_key; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.appuser
    ADD CONSTRAINT appuser_phone_key UNIQUE (phone);


--
-- Name: appuser appuser_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.appuser
    ADD CONSTRAINT appuser_pkey PRIMARY KEY (userid);


--
-- Name: family family_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_pkey PRIMARY KEY (familyid);


--
-- Name: familyhistory familyhistory_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familyhistory
    ADD CONSTRAINT familyhistory_pkey PRIMARY KEY (historyid);


--
-- Name: familymember familymember_email_key; Type: CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_email_key UNIQUE (email);


--
-- Name: familymember familymember_logonname_key; Type: CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_logonname_key UNIQUE (logonname);


--
-- Name: familymember familymember_phone_key; Type: CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_phone_key UNIQUE (phone);


--
-- Name: familymember familymember_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_pkey PRIMARY KEY (memberid);


--
-- Name: familymemberhistory familymemberhistory_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymemberhistory
    ADD CONSTRAINT familymemberhistory_pkey PRIMARY KEY (historyid);


--
-- Name: lkpconfig lkpconfig_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkpconfig
    ADD CONSTRAINT lkpconfig_pkey PRIMARY KEY (configname, paramname);


--
-- Name: lkpoperation lkpoperation_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkpoperation
    ADD CONSTRAINT lkpoperation_pkey PRIMARY KEY (operationid);


--
-- Name: lkprole lkprole_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkprole
    ADD CONSTRAINT lkprole_pkey PRIMARY KEY (roleid);


--
-- Name: lkproleoperationmap lkproleoperationmap_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_pkey PRIMARY KEY (roleid, operationid);


--
-- Name: memberrelationship memberrelationship_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_pkey PRIMARY KEY (memberid, relatedmemberid);


--
-- Name: mstuserrolemap mstuserrolemap_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_pkey PRIMARY KEY (userid, roleid);


--
-- Name: otpverification otpverification_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.otpverification
    ADD CONSTRAINT otpverification_pkey PRIMARY KEY (emailid, otptype);


--
-- Name: otpverificationhistory otpverificationhistory_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.otpverificationhistory
    ADD CONSTRAINT otpverificationhistory_pkey PRIMARY KEY (seqid);


--
-- Name: samaj samaj_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.samaj
    ADD CONSTRAINT samaj_pkey PRIMARY KEY (samajid);


--
-- Name: txtsession txtsession_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.txtsession
    ADD CONSTRAINT txtsession_pkey PRIMARY KEY (sessionid);


--
-- Name: txtsessiontxn txtsessiontxn_pkey; Type: CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_pkey PRIMARY KEY (txnid);


--
-- Name: address address_createdby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.address
    ADD CONSTRAINT address_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: address address_lastupdatedby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.address
    ADD CONSTRAINT address_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: family family_addressid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_addressid_fkey FOREIGN KEY (addressid) REFERENCES shared_schema.address(addressid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: family family_createdby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: family family_lastupdatedby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: family family_samajid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_samajid_fkey FOREIGN KEY (samajid) REFERENCES shared_schema.samaj(samajid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: familymember familymember_createdby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: familymember familymember_familyid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_familyid_fkey FOREIGN KEY (familyid) REFERENCES shared_schema.family(familyid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: familymember familymember_lastupdatedby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: familymember familymember_memberaddressid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_memberaddressid_fkey FOREIGN KEY (memberaddressid) REFERENCES shared_schema.address(addressid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: familymember familymember_samajid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_samajid_fkey FOREIGN KEY (samajid) REFERENCES shared_schema.samaj(samajid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: lkpconfig lkpconfig_createdby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkpconfig
    ADD CONSTRAINT lkpconfig_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: lkpconfig lkpconfig_lastupdatedby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkpconfig
    ADD CONSTRAINT lkpconfig_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: lkpoperation lkpoperation_createdby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkpoperation
    ADD CONSTRAINT lkpoperation_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: lkpoperation lkpoperation_lastupdatedby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkpoperation
    ADD CONSTRAINT lkpoperation_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: lkprole lkprole_createdby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkprole
    ADD CONSTRAINT lkprole_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: lkprole lkprole_lastupdatedby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkprole
    ADD CONSTRAINT lkprole_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: lkproleoperationmap lkproleoperationmap_createdby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: lkproleoperationmap lkproleoperationmap_lastupdatedby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: lkproleoperationmap lkproleoperationmap_operationid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_operationid_fkey FOREIGN KEY (operationid) REFERENCES shared_schema.lkpoperation(operationid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: lkproleoperationmap lkproleoperationmap_roleid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_roleid_fkey FOREIGN KEY (roleid) REFERENCES shared_schema.lkprole(roleid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: memberrelationship memberrelationship_createdby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: memberrelationship memberrelationship_lastupdatedby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: memberrelationship memberrelationship_memberid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_memberid_fkey FOREIGN KEY (memberid) REFERENCES shared_schema.familymember(memberid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: memberrelationship memberrelationship_relatedmemberid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: postgres
--

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_relatedmemberid_fkey FOREIGN KEY (relatedmemberid) REFERENCES shared_schema.familymember(memberid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: mstuserrolemap mstuserrolemap_createdby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: mstuserrolemap mstuserrolemap_lastupdatedby_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: mstuserrolemap mstuserrolemap_roleid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_roleid_fkey FOREIGN KEY (roleid) REFERENCES shared_schema.lkprole(roleid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: mstuserrolemap mstuserrolemap_userid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_userid_fkey FOREIGN KEY (userid) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: txtsession txtsession_userid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.txtsession
    ADD CONSTRAINT txtsession_userid_fkey FOREIGN KEY (userid) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: txtsessiontxn txtsessiontxn_operationid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_operationid_fkey FOREIGN KEY (operationid) REFERENCES shared_schema.lkpoperation(operationid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: txtsessiontxn txtsessiontxn_sessionid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_sessionid_fkey FOREIGN KEY (sessionid) REFERENCES shared_schema.txtsession(sessionid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: txtsessiontxn txtsessiontxn_userid_fkey; Type: FK CONSTRAINT; Schema: shared_schema; Owner: familytree_master
--

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_userid_fkey FOREIGN KEY (userid) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: SCHEMA shared_schema; Type: ACL; Schema: -; Owner: familytree_master
--

GRANT USAGE ON SCHEMA shared_schema TO familytree_app_role;


--
-- Name: TABLE address; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.address TO familytree_app_role;


--
-- Name: SEQUENCE address_addressid_seq; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT ALL ON SEQUENCE shared_schema.address_addressid_seq TO familytree_app_role;


--
-- Name: TABLE appuser; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.appuser TO familytree_app_role;


--
-- Name: SEQUENCE appuser_userid_seq; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT ALL ON SEQUENCE shared_schema.appuser_userid_seq TO familytree_app_role;


--
-- Name: TABLE family; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.family TO familytree_app_role;


--
-- Name: SEQUENCE family_familyid_seq; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT ALL ON SEQUENCE shared_schema.family_familyid_seq TO familytree_app_role;


--
-- Name: TABLE familyhistory; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familyhistory TO familytree_app_role;


--
-- Name: SEQUENCE familyhistory_familyid_seq; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT ALL ON SEQUENCE shared_schema.familyhistory_familyid_seq TO familytree_app_role;


--
-- Name: SEQUENCE familyhistory_historyid_seq; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT ALL ON SEQUENCE shared_schema.familyhistory_historyid_seq TO familytree_app_role;


--
-- Name: TABLE familymember; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familymember TO familytree_app_role;


--
-- Name: SEQUENCE familymember_memberid_seq; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT ALL ON SEQUENCE shared_schema.familymember_memberid_seq TO familytree_app_role;


--
-- Name: TABLE familymemberhistory; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familymemberhistory TO familytree_app_role;


--
-- Name: SEQUENCE familymemberhistory_historyid_seq; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT ALL ON SEQUENCE shared_schema.familymemberhistory_historyid_seq TO familytree_app_role;


--
-- Name: TABLE lkpconfig; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.lkpconfig TO familytree_app_role;


--
-- Name: TABLE lkpoperation; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.lkpoperation TO familytree_app_role;


--
-- Name: TABLE lkprole; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.lkprole TO familytree_app_role;


--
-- Name: TABLE lkproleoperationmap; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.lkproleoperationmap TO familytree_app_role;


--
-- Name: TABLE memberrelationship; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.memberrelationship TO familytree_app_role;


--
-- Name: TABLE mstuserrolemap; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.mstuserrolemap TO familytree_app_role;


--
-- Name: TABLE otpverification; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.otpverification TO familytree_app_role;


--
-- Name: TABLE otpverificationhistory; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.otpverificationhistory TO familytree_app_role;


--
-- Name: SEQUENCE otpverificationhistory_seqid_seq; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT ALL ON SEQUENCE shared_schema.otpverificationhistory_seqid_seq TO familytree_app_role;


--
-- Name: TABLE samaj; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.samaj TO familytree_app_role;


--
-- Name: SEQUENCE samaj_samajid_seq; Type: ACL; Schema: shared_schema; Owner: postgres
--

GRANT ALL ON SEQUENCE shared_schema.samaj_samajid_seq TO familytree_app_role;


--
-- Name: TABLE txtsession; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.txtsession TO familytree_app_role;


--
-- Name: SEQUENCE txtsession_sessionid_seq; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT ALL ON SEQUENCE shared_schema.txtsession_sessionid_seq TO familytree_app_role;


--
-- Name: TABLE txtsessiontxn; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.txtsessiontxn TO familytree_app_role;


--
-- Name: SEQUENCE txtsessiontxn_txnid_seq; Type: ACL; Schema: shared_schema; Owner: familytree_master
--

GRANT ALL ON SEQUENCE shared_schema.txtsessiontxn_txnid_seq TO familytree_app_role;


--
-- PostgreSQL database dump complete
--

