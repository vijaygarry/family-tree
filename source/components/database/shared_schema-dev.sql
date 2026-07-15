-- PostgreSQL database dump

-- Dumped from database version 15.4
-- Dumped by pg_dump version 15.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE SCHEMA shared_schema;

ALTER SCHEMA shared_schema OWNER TO familytree_master;

SET default_tablespace = '';

SET default_table_access_method = heap;

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

CREATE SEQUENCE shared_schema.address_addressid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.address_addressid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.address_addressid_seq OWNED BY shared_schema.address.addressid;

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

CREATE SEQUENCE shared_schema.appuser_userid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.appuser_userid_seq OWNER TO familytree_master;

ALTER SEQUENCE shared_schema.appuser_userid_seq OWNED BY shared_schema.appuser.userid;

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

CREATE SEQUENCE shared_schema.family_familyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.family_familyid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.family_familyid_seq OWNED BY shared_schema.family.familyid;

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

CREATE SEQUENCE shared_schema.familyhistory_familyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.familyhistory_familyid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.familyhistory_familyid_seq OWNED BY shared_schema.familyhistory.familyid;

CREATE SEQUENCE shared_schema.familyhistory_historyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.familyhistory_historyid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.familyhistory_historyid_seq OWNED BY shared_schema.familyhistory.historyid;

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

CREATE SEQUENCE shared_schema.familymember_memberid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.familymember_memberid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.familymember_memberid_seq OWNED BY shared_schema.familymember.memberid;

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

CREATE SEQUENCE shared_schema.familymemberhistory_historyid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.familymemberhistory_historyid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.familymemberhistory_historyid_seq OWNED BY shared_schema.familymemberhistory.historyid;

CREATE TABLE shared_schema.familymemberregistration (
    memberrequestid integer NOT NULL,
    familyrequestid integer NOT NULL,
    samajid smallint NOT NULL,
    headoffamily boolean DEFAULT false NOT NULL,
    firstname character varying(100) NOT NULL,
    firstnameinhindi character varying(100),
    gender character varying(10),
    birthday smallint,
    birthmonth smallint NOT NULL,
    birthyear smallint NOT NULL,
    maritalstatus character varying(20) NOT NULL,
    weddingdate date,
    phone character varying(20),
    email character varying(100),
    addresssameasfamily boolean DEFAULT true NOT NULL,
    educationdetails character varying(255),
    occupation character varying(255),
    createddate timestamp with time zone NOT NULL,
    memberid integer,
    relationshiptype character varying(100) NOT NULL,
    relatedmemberid integer NOT NULL
);

ALTER TABLE shared_schema.familymemberregistration OWNER TO postgres;

CREATE SEQUENCE shared_schema.familymemberregistration_memberrequestid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.familymemberregistration_memberrequestid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.familymemberregistration_memberrequestid_seq OWNED BY shared_schema.familymemberregistration.memberrequestid;

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

CREATE TABLE shared_schema.lkproleoperationmap (
    roleid character varying(55) NOT NULL,
    operationid character varying(55) NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);

ALTER TABLE shared_schema.lkproleoperationmap OWNER TO familytree_master;

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

CREATE TABLE shared_schema.mstuserrolemap (
    userid integer NOT NULL,
    roleid character varying(100) NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);

ALTER TABLE shared_schema.mstuserrolemap OWNER TO familytree_master;

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

CREATE SEQUENCE shared_schema.otpverificationhistory_seqid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.otpverificationhistory_seqid_seq OWNER TO familytree_master;

ALTER SEQUENCE shared_schema.otpverificationhistory_seqid_seq OWNED BY shared_schema.otpverificationhistory.seqid;

CREATE TABLE shared_schema.samaj (
    samajid smallint NOT NULL,
    samajname character varying(255) NOT NULL,
    contactdetails character varying(255)
);

ALTER TABLE shared_schema.samaj OWNER TO postgres;

CREATE SEQUENCE shared_schema.samaj_samajid_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.samaj_samajid_seq OWNER TO postgres;

ALTER SEQUENCE shared_schema.samaj_samajid_seq OWNED BY shared_schema.samaj.samajid;

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

CREATE SEQUENCE shared_schema.txtsession_sessionid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.txtsession_sessionid_seq OWNER TO familytree_master;

ALTER SEQUENCE shared_schema.txtsession_sessionid_seq OWNED BY shared_schema.txtsession.sessionid;

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

CREATE SEQUENCE shared_schema.txtsessiontxn_txnid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.txtsessiontxn_txnid_seq OWNER TO familytree_master;

ALTER SEQUENCE shared_schema.txtsessiontxn_txnid_seq OWNED BY shared_schema.txtsessiontxn.txnid;

ALTER TABLE ONLY shared_schema.address ALTER COLUMN addressid SET DEFAULT nextval('shared_schema.address_addressid_seq'::regclass);

ALTER TABLE ONLY shared_schema.appuser ALTER COLUMN userid SET DEFAULT nextval('shared_schema.appuser_userid_seq'::regclass);

ALTER TABLE ONLY shared_schema.family ALTER COLUMN familyid SET DEFAULT nextval('shared_schema.family_familyid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familyhistory ALTER COLUMN historyid SET DEFAULT nextval('shared_schema.familyhistory_historyid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familyhistory ALTER COLUMN familyid SET DEFAULT nextval('shared_schema.familyhistory_familyid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familymember ALTER COLUMN memberid SET DEFAULT nextval('shared_schema.familymember_memberid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familymemberhistory ALTER COLUMN historyid SET DEFAULT nextval('shared_schema.familymemberhistory_historyid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familymemberregistration ALTER COLUMN memberrequestid SET DEFAULT nextval('shared_schema.familymemberregistration_memberrequestid_seq'::regclass);

ALTER TABLE ONLY shared_schema.familyregistrationrequest ALTER COLUMN familyrequestid SET DEFAULT nextval('shared_schema.familyregistrationrequest_familyrequestid_seq'::regclass);

ALTER TABLE ONLY shared_schema.otpverificationhistory ALTER COLUMN seqid SET DEFAULT nextval('shared_schema.otpverificationhistory_seqid_seq'::regclass);

ALTER TABLE ONLY shared_schema.samaj ALTER COLUMN samajid SET DEFAULT nextval('shared_schema.samaj_samajid_seq'::regclass);

ALTER TABLE ONLY shared_schema.txtsession ALTER COLUMN sessionid SET DEFAULT nextval('shared_schema.txtsession_sessionid_seq'::regclass);

ALTER TABLE ONLY shared_schema.txtsessiontxn ALTER COLUMN txnid SET DEFAULT nextval('shared_schema.txtsessiontxn_txnid_seq'::regclass);

INSERT INTO shared_schema.address (addressid, addressline1, addressline2, addressline3, city, district, state, postalcode, country, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (0, 'Same as family', NULL, NULL, 'Same as family', NULL, 'Same as family', 'Family', 'Same as family', 1, '2025-10-12 00:00:00-04', 1, '2025-10-12 00:00:00-04');
INSERT INTO shared_schema.address (addressid, addressline1, addressline2, addressline3, city, district, state, postalcode, country, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (189, 'Bungla No 2', 'Andheri West', NULL, 'Mumbai', NULL, 'Maharashtra', '400101', 'India', 2, '2025-12-07 12:43:45.426-05', 2, '2025-12-07 12:43:45.426-05');
INSERT INTO shared_schema.address (addressid, addressline1, addressline2, addressline3, city, district, state, postalcode, country, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (190, 'Bungla No 4', 'Juhu', NULL, 'Mumbai', NULL, 'Maharashtra', '400102', 'India', 12, '2025-12-09 21:49:24.848-05', 12, '2025-12-09 21:49:24.848-05');
INSERT INTO shared_schema.address (addressid, addressline1, addressline2, addressline3, city, district, state, postalcode, country, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (191, 'Bungla No 3', 'Hiranandani Pawai', NULL, 'Mumbai', NULL, 'Maharashtra', '400201', 'India', 12, '2025-12-10 21:33:39.46-05', 12, '2025-12-10 21:33:39.46-05');
INSERT INTO shared_schema.address (addressid, addressline1, addressline2, addressline3, city, district, state, postalcode, country, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (196, 'Temporary', NULL, NULL, 'Amravati', NULL, 'Maharashtra', '444601', 'India', 2, '2026-02-22 22:32:20.47-05', 2, '2026-02-22 22:32:20.47-05');

INSERT INTO shared_schema.appuser (userid, logonname, hashpassword, firstname, lastname, emailid, phone, authenticationtype, singlesignonid, invalidloginattempts, lastlogintime, lastpasswordchangetime, status, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (1, 'system', '$argon2id$v=19$m=16384,t=2,p=1$7arh7YIIjnMLvW0KrKxw1A$+Z9UIdQ8MUuf40V38N8v8ZZNAEGY59FsItIczNL5+JQ', 'System', 'User', 'system@chippasamaj.com', NULL, 'DB_PWD', NULL, 0, NULL, NULL, 'I', 1, '2025-10-12 00:00:00-04', 1, '2025-10-12 00:00:00-04');
INSERT INTO shared_schema.appuser (userid, logonname, hashpassword, firstname, lastname, emailid, phone, authenticationtype, singlesignonid, invalidloginattempts, lastlogintime, lastpasswordchangetime, status, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (14, 'member-101', '$argon2id$v=19$m=16384,t=2,p=1$7arh7YIIjnMLvW0KrKxw1A$+Z9UIdQ8MUuf40V38N8v8ZZNAEGY59FsItIczNL5+JQ', 'Admin', 'Temporary', 'rajputchhipasamaj@gmail.com', NULL, 'ROLE_BASE', NULL, 0, NULL, NULL, 'A', 14, '2026-02-22 22:35:35.19-05', 14, '2026-02-22 22:44:02.653929-05');
INSERT INTO shared_schema.appuser (userid, logonname, hashpassword, firstname, lastname, emailid, phone, authenticationtype, singlesignonid, invalidloginattempts, lastlogintime, lastpasswordchangetime, status, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (12, 'guest', '$argon2id$v=19$m=16384,t=2,p=1$7arh7YIIjnMLvW0KrKxw1A$+Z9UIdQ8MUuf40V38N8v8ZZNAEGY59FsItIczNL5+JQ', 'Akshay', 'Kumar', NULL, '911112223334', 'ROLE_BASE', NULL, 0, NULL, NULL, 'L', 12, '2025-12-07 12:52:19.81-05', 12, '2026-02-18 18:56:10.882912-05');
INSERT INTO shared_schema.appuser (userid, logonname, hashpassword, firstname, lastname, emailid, phone, authenticationtype, singlesignonid, invalidloginattempts, lastlogintime, lastpasswordchangetime, status, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (2, 'member-1', '$argon2id$v=19$m=16384,t=2,p=1$7arh7YIIjnMLvW0KrKxw1A$+Z9UIdQ8MUuf40V38N8v8ZZNAEGY59FsItIczNL5+JQ', 'Vijay', 'Garothaya', 'vijay_garry@hotmail.com', '15714843763', 'ROLE_BASE', NULL, 0, NULL, NULL, 'A', 2, '2025-10-12 21:14:42.134-04', 2, '2026-07-07 20:41:38.873051-04');

INSERT INTO shared_schema.family (familyid, samajid, familyname, familynameinhindi, gotra, addressid, region, phone, isphonewhatsappregistered, email, familysearchtext, active, familyimage, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (18, 3, 'Temporary', NULL, 'Temporary', 196, 'Amravati, Maharashtra', NULL, false, NULL, 'Temporary Amravati, Maharashtra', true, 'uploads/family/sample-family-image.png', '2026-02-22 22:32:20.47-05', 2, '2026-02-22 22:32:20.47-05', 2, '2026-02-22 22:34:18.6-05');
INSERT INTO shared_schema.family (familyid, samajid, familyname, familynameinhindi, gotra, addressid, region, phone, isphonewhatsappregistered, email, familysearchtext, active, familyimage, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (11, 2, 'Kumar', 'कुमार', 'Kumar', 189, 'Mumbai, Maharashtra', '911112223334', false, 'kumar@rajputchhipa.com', 'Kumar कुमार 911112223334 kumar@rajputchhipa.com Mumbai, Maharashtra', true, 'uploads//family//family_11_1765508956947.jpeg', '2025-12-11 22:09:16.944-05', 2, '2025-12-07 12:43:45.426-05', 12, '2026-02-17 22:10:35.756-05');
INSERT INTO shared_schema.family (familyid, samajid, familyname, familynameinhindi, gotra, addressid, region, phone, isphonewhatsappregistered, email, familysearchtext, active, familyimage, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (12, 2, 'Khanna', 'खन्ना', 'Khanna', 190, 'Mumbai, Maharashtra', '912223334445', false, 'khanna@rajputchhipa.com', 'Khanna खन्ना 912223334445 khanna@rajputchhipa.com Mumbai, Maharashtra', true, 'uploads//family//family_12_1765509053764.jpeg', '2025-12-11 22:10:53.761-05', 12, '2025-12-09 21:49:24.848-05', 12, '2025-12-11 22:10:53.761-05');
INSERT INTO shared_schema.family (familyid, samajid, familyname, familynameinhindi, gotra, addressid, region, phone, isphonewhatsappregistered, email, familysearchtext, active, familyimage, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (13, 2, 'Hiranandani', 'हीरानंदानी', 'Hiranandani', 191, 'Mumbai, Maharashtra', NULL, false, 'hiranandani@rajputchhipa.com', 'Hiranandani हीरानंदानी hiranandani@rajputchhipa.com Mumbai, Maharashtra', true, 'uploads/family/sample-family-image.png', '2025-12-10 21:33:39.46-05', 12, '2025-12-10 21:33:39.46-05', 12, '2025-12-11 21:59:18.431-05');

INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (74, 13, 2, NULL, true, 'Surendra', 'सुरेंद्र', 'Hiranandani', NULL, NULL, NULL, 'Male', 1, 1, 1954, 'Married', NULL, NULL, NULL, false, false, 'surendra@rajputchhipa.com', false, true, 0, 'MBA', 'entrepreneur, Founder of Hiranandani Group', NULL, 'Surendra सुरेंद्र Hiranandani surendra@rajputchhipa.com ', 'uploads//member//member_74_1765424041819.jpg', 'uploads//member//member_74_1765424041819.jpg', '2025-12-10 22:34:01.801-05', 12, '2025-12-10 21:35:31.723-05', 12, '2025-12-11 21:58:53.24-05');
INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (69, 11, 2, NULL, false, 'Aarav', 'आरव', 'Kumar', NULL, NULL, NULL, 'Male', 15, 9, 2002, 'Single', NULL, NULL, NULL, false, false, NULL, false, true, 0, 'High School', 'Student', NULL, 'Aarav Kumar आरव Mumbai, Maharashtra', 'uploads//member//member_69_1765424018237.jpg', 'uploads//member//member_69_1765424018237.jpg', '2025-12-10 22:33:38.235-05', 12, '2025-12-09 21:38:25.471-05', 12, '2025-12-10 22:33:38.235-05');
INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (65, 11, 2, 'guest', true, 'Akshay', 'अक्षय', 'Kumar', NULL, 'Aki', NULL, 'Male', 9, 9, 1967, 'Married', '2001-01-17', NULL, '911112223334', true, false, 'akshay@rajputchhipa.com', false, true, 0, 'B. COM Khalsa College, Mumbai University', 'Actor and Film producer', NULL, 'Akshay Kumar अक्षय Aki 911112223334 akshay@rajputchhipa.com Mumbai, Maharashtra ', 'uploads//member//member_65_1765421174788.jpg', 'uploads//member//member_65_1765421174788.jpg', '2025-12-10 21:46:14.759-05', 2, '2025-12-07 12:50:43.549-05', 12, '2026-02-17 22:10:35.756-05');
INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (101, 18, 3, 'member-101', true, 'Admin', NULL, 'Temporary', NULL, NULL, NULL, 'Male', -1, 1, 2026, 'Single', NULL, NULL, NULL, false, false, 'rajputchhipasamaj@gmail.com', true, true, 0, NULL, NULL, NULL, 'Admin Temporary rajputchhipasamaj@gmail.com Amravati, Maharashtra ', 'uploads/member/kid_boy_avatar.jpg', 'uploads/member/kid_boy_avatar.jpg', '2026-02-22 22:34:18.6-05', 2, '2026-02-22 22:34:18.6-05', 14, '2026-02-22 22:35:35.19-05');
INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (68, 11, 2, NULL, false, 'Twinkle', 'ट्विंकल', 'Kumar', 'Khanna', NULL, NULL, 'Female', 29, 12, 1973, 'Married', '2001-01-17', NULL, NULL, false, false, 'twinkle@rajputchhipa.com', false, true, 0, 'BCom, MA', 'Former actress', NULL, 'Twinkle Kumar ट्विंकल Khanna twinkle@rajputchhipa.com Mumbai, Maharashtra', 'uploads//member//member_68_1765421201960.jpg', 'uploads//member//member_68_1765421201960.jpg', '2025-12-10 21:46:41.957-05', 12, '2025-12-09 21:34:01.242-05', 12, '2025-12-10 21:46:41.957-05');
INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (71, 12, 2, NULL, true, 'Dimple', 'डिंपल', 'Khanna', 'Kapadia', NULL, NULL, 'Female', 8, 6, 1957, 'Married', '1973-11-11', NULL, NULL, false, false, 'dimple@rajputchhipa.com', false, true, 0, 'High School', 'Actress and film producer', NULL, 'Dimple Khanna डिंपल Kapadia dimple@rajputchhipa.com Mumbai, Maharashtra', 'uploads//member//member_71_1765509209238.jpg', 'uploads//member//member_71_1765509209238.jpg', '2025-12-11 22:13:29.236-05', 12, '2025-12-09 21:56:20.944-05', 12, '2025-12-11 22:13:29.236-05');
INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (73, 12, 2, NULL, false, 'Rinke', NULL, 'Khanna', NULL, NULL, NULL, 'Female', 27, 7, 1977, 'Married', NULL, NULL, NULL, false, false, NULL, false, true, 0, 'BCom', 'Actress', NULL, 'Rinke Khanna Mumbai, Maharashtra', 'uploads//member//member_73_1765509248925.jpg', 'uploads//member//member_73_1765509248925.jpg', '2025-12-11 22:14:08.924-05', 12, '2025-12-09 22:01:40.107-05', 12, '2025-12-11 22:14:08.924-05');
INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (75, 13, 2, NULL, false, 'Alka', NULL, 'Hiranandani', 'Kumar', NULL, NULL, 'Female', 2, 2, 1970, 'Married', NULL, NULL, NULL, false, false, NULL, false, true, 0, 'MBA', 'Film Producer', NULL, 'Alka Hiranandani Kumar Mumbai, Maharashtra', 'uploads//member//member_75_1765424056212.jpg', 'uploads//member//member_75_1765424056212.jpg', '2025-12-10 22:34:16.21-05', 12, '2025-12-10 21:36:55.339-05', 12, '2025-12-10 22:34:16.21-05');
INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (70, 11, 2, NULL, false, 'Nitara', 'नितारा', 'Kumar', NULL, NULL, NULL, 'Female', 25, 9, 2012, 'Single', NULL, NULL, NULL, false, false, NULL, false, true, 0, 'High School', 'Student', NULL, 'Nitara Kumar नितारा Mumbai, Maharashtra', 'uploads//member//member_70_1765425154052.jpg', 'uploads//member//member_70_1765425154052.jpg', '2025-12-10 22:52:34.05-05', 12, '2025-12-09 21:40:11.192-05', 12, '2025-12-10 22:52:34.05-05');
INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (67, 11, 2, NULL, false, 'Aruna', 'अरुणा', 'Kumar', NULL, NULL, NULL, 'Female', 10, 2, 1942, 'Married', NULL, '2021-09-08', NULL, false, false, NULL, false, true, 0, 'Graduate', 'Film producer', NULL, 'Aruna Kumar अरुणा Mumbai, Maharashtra', 'uploads//member//member_67_1765423878136.jpg', 'uploads//member//member_67_1765423878136.jpg', '2025-12-10 22:31:18.134-05', 12, '2025-12-07 13:10:57.443-05', 1, '2025-12-11 07:55:24.723364-05');
INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (72, 12, 2, NULL, false, 'Rajesh', 'राजेश', 'Khanna', NULL, 'Kaka', 'काका', 'Male', 29, 12, 1942, 'Married', '1973-06-27', '2012-07-18', NULL, false, false, NULL, false, true, 0, 'BCom', 'Film Actor', NULL, 'Rajesh Khanna राजेश Kaka Mumbai, Maharashtra', 'uploads//member//member_72_1765509112685.jpg', 'uploads//member//member_72_1765509112685.jpg', '2025-12-11 22:11:52.682-05', 12, '2025-12-09 21:59:18.161-05', 12, '2025-12-11 22:11:52.682-05');
INSERT INTO shared_schema.familymember (memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, maritalstatus, weddingdate, dateofdeath, phone, isphoneverified, isphonewhatsappregistered, email, isemailverified, addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (66, 11, 2, NULL, false, 'Hari Om', 'हरि', 'Kumar', NULL, NULL, NULL, 'Male', 9, 9, 1940, 'Married', NULL, '1999-10-13', NULL, false, false, NULL, false, true, 0, 'Graduate', 'Indian Army officer', NULL, 'Hari Om Kumar हरि Mumbai, Maharashtra', 'uploads//member//member_66_1765510411368.jpg', 'uploads//member//member_66_1765510411368.jpg', '2025-12-11 22:33:31.35-05', 12, '2025-12-07 13:06:39.145-05', 12, '2025-12-11 22:33:31.35-05');

INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('LOGIN', 'Login to application - authenticate user and create session', 'UserLogin', false, true, 'NO_AUTHORIZATION', true, 1, '2025-04-25 17:45:57.739838-04', 1, '2025-04-25 17:45:57.739838-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('LOGOUT', 'Logout User - Destroy the current session', 'UserLogout', false, true, 'NO_AUTHORIZATION', true, 1, '2025-04-25 17:45:57.739838-04', 1, '2025-04-25 17:45:57.739838-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('IS_SESSION_VALID', 'Is session valid', 'IsUserSessionValid', false, true, 'NO_AUTHORIZATION', true, 1, '2025-04-25 17:45:57.739838-04', 1, '2025-04-25 17:45:57.739838-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('CHANGE_PASSWORD', 'Change user password', 'ChangeUserPassword', true, true, 'ROLE_BASE', true, 1, '2025-04-25 17:45:57.739838-04', 1, '2025-04-25 17:45:57.739838-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('GET_MEMBER_PROFILE', 'Get family member profile', 'GetFamilyMemberProfile', true, true, 'ROLE_BASE', true, 1, '2025-04-25 17:45:57.739838-04', 1, '2025-04-25 17:45:57.739838-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('ADD_FAMILY', 'Add Family', 'AddFamilyBean', true, true, 'ROLE_BASE', true, 1, '2025-05-30 01:22:55.431334-04', 1, '2025-05-30 01:22:55.431334-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('GET_FAMILY_DETAILS', 'Get family details', 'GetFamilyDetails', true, true, 'ROLE_BASE', true, 1, '2025-04-25 17:45:57.739838-04', 1, '2025-04-25 17:45:57.739838-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('GET_SESSION_DETAILS', 'Get session details', 'GetUserSessionDetails', true, true, 'ROLE_BASE', true, 1, '2025-04-25 17:45:57.739838-04', 1, '2025-04-25 17:45:57.739838-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SEARCH_FAMILY', 'Search family', 'SearchFamilyOperation', true, true, 'ROLE_BASE', true, 1, '2025-07-09 22:34:54.161589-04', 1, '2025-07-09 22:34:54.161589-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('GET_EVENTS', 'Get events', 'GetEvents', true, true, 'ROLE_BASE', true, 1, '2025-07-22 21:47:32.05564-04', 1, '2025-07-22 21:47:32.05564-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('MANAGE_RELATIONSHIP', 'Manage Relationship', 'ManageRelationship', true, true, 'ROLE_BASE', true, 1, '2025-08-02 15:34:42.826237-04', 1, '2025-08-02 15:34:42.826237-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FORGOT_PASSWORD_REQUEST_OTP', 'Request OTP for forgot password', 'RequestForgotPasswordOTPOperation', false, true, 'NO_AUTHORIZATION', true, 1, '2025-08-16 16:07:07.511868-04', 1, '2025-08-16 16:07:07.511868-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('RESET_FORGOT_PASSWORD', 'Reset forgot password', 'ResetForgotPasswordOperation', false, true, 'NO_AUTHORIZATION', true, 1, '2025-08-16 16:07:07.511868-04', 1, '2025-08-16 16:07:07.511868-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SIGN_UP_REQUEST_OTP', 'Request OTP for Sign Up', 'RequestSignUpOTPOperation', false, true, 'NO_AUTHORIZATION', true, 1, '2025-08-16 17:47:07.081823-04', 1, '2025-08-16 17:47:07.081823-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SIGN_UP', 'Sign Up User', 'SignUpOperation', false, true, 'NO_AUTHORIZATION', true, 1, '2025-08-16 17:47:07.081823-04', 1, '2025-08-16 17:47:07.081823-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('WHO_AM_I', 'Get session details with member Id', 'WhoAmIOperation', true, true, 'ROLE_BASE', true, 1, '2025-08-25 05:24:18.920117-04', 1, '2025-08-25 05:24:18.920117-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('UPDATE_MY_FAMILY_IMAGE', 'Update my and related family image', 'updateFamilyImage', true, true, 'ROLE_BASE', true, 1, '2025-09-08 06:12:42.369338-04', 1, '2025-09-08 06:12:42.369338-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('UPDATE_ANY_FAMILY_IMAGE', 'Update any family image', 'updateFamilyImage', true, true, 'ROLE_BASE', true, 1, '2025-09-08 06:12:42.369338-04', 1, '2025-09-08 06:12:42.369338-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('UPDATE_MY_FAMILY_DETAILS', 'Update my family details', 'updateFamilyDetails', true, true, 'ROLE_BASE', true, 1, '2025-09-15 04:27:08.994211-04', 1, '2025-09-15 04:27:08.994211-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('UPDATE_MY_FAMILY_MEMBER', 'Update my member details', 'updateFamilyMemberDetails', true, true, 'ROLE_BASE', true, 1, '2025-09-15 04:27:08.994211-04', 1, '2025-09-15 04:27:08.994211-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('UPDATE_MY_FAMILY_MEMBER_IMAGE', 'Update my member image', 'updateFamilyMemberImage', true, true, 'ROLE_BASE', true, 1, '2025-10-05 11:52:13.766259-04', 1, '2025-10-05 11:52:13.766259-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('ADD_MEMBER_TO_MY_FAMILY', 'Add Family Member', 'AddFamilyMemberBean', true, true, 'ROLE_BASE', true, 1, '2025-10-14 21:55:32.453655-04', 1, '2025-10-14 21:55:32.453655-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('ADD_MEMBER_TO_ANY_FAMILY', 'Add Family Member', 'AddFamilyMemberBean', true, true, 'ROLE_BASE', true, 1, '2025-10-14 21:55:32.456512-04', 1, '2025-10-14 21:55:32.456512-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('UPDATE_ANY_FAMILY_DETAILS', 'Update any family details', 'updateFamilyDetails', true, true, 'ROLE_BASE', true, 1, '2025-10-14 21:55:32.457944-04', 1, '2025-10-14 21:55:32.457944-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('UPDATE_ANY_FAMILY_MEMBER', 'Update any member details', 'updateFamilyMemberDetails', true, true, 'ROLE_BASE', true, 1, '2025-10-14 21:55:32.459439-04', 1, '2025-10-14 21:55:32.459439-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('GET_ACCOUNT_LIST', 'Get Account List', 'GetAccountList', true, true, 'ROLE_BASE', true, 1, '2025-07-25 18:56:26.888793-04', 1, '2025-07-25 18:56:26.888793-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('GET_ACCOUNT_STATEMENT', 'Get Account Statement', 'GetAccountStatement', true, true, 'ROLE_BASE', true, 1, '2025-07-25 18:56:26.888793-04', 1, '2025-07-25 18:56:26.888793-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SEARCH_FAMILY_MEMBER', 'Search family member', 'searchFamilyMember', true, true, 'ROLE_BASE', true, 1, '2026-01-06 00:22:03.79478-05', 1, '2026-01-06 00:22:03.79478-05');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('REGISTER_FAMILY', 'Family Registration Request', 'FamilyRegistrationOperation', false, true, 'NO_AUTHORIZATION', true, 1, '2026-03-15 22:40:57.350409-04', 1, '2026-03-15 22:40:57.350409-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('PROCESS_FAMILY_REGISTRATION', 'Process family registration', 'processFamilyRegistration', true, true, 'ROLE_BASE', true, 1, '2026-04-04 21:39:00.346332-04', 1, '2026-04-04 21:39:00.346332-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('GET_SAMAJ_STATISTICS', 'Get Samaj Statistics', 'getSamajStatistics', false, false, 'NO_AUTHORIZATION', true, 1, '2026-04-23 21:51:45.960164-04', 1, '2026-04-23 21:51:45.960164-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('GET_FAMILY_COUNT_BY_CITY', 'Get family count by city', 'GET_FAMILY_COUNT_BY_CITY', true, true, 'ROLE_BASE', true, 1, '2026-05-25 11:00:21.357743-04', 1, '2026-05-25 11:00:21.357743-04');
INSERT INTO shared_schema.lkpoperation (operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, active, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('GET_FAMILIES_BY_REGION', 'Get list of families for given region', 'GET_FAMILIES_BY_REGION', true, true, 'ROLE_BASE', true, 1, '2026-05-25 11:00:21.366703-04', 1, '2026-05-25 11:00:21.366703-04');

INSERT INTO shared_schema.lkprole (roleid, roledesc, enable, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'Generic role for all registered user', true, 1, '2025-04-26 00:09:55.620161-04', 1, '2025-04-26 00:09:55.620161-04');
INSERT INTO shared_schema.lkprole (roleid, roledesc, enable, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'Generic role and manages own family', true, 1, '2025-04-26 00:09:55.620161-04', 1, '2025-04-26 00:09:55.620161-04');
INSERT INTO shared_schema.lkprole (roleid, roledesc, enable, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'Generic role and manages any family', true, 1, '2025-04-26 00:09:55.620161-04', 1, '2025-04-26 00:09:55.620161-04');
INSERT INTO shared_schema.lkprole (roleid, roledesc, enable, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'Super Admin', true, 1, '2025-04-26 00:09:55.620161-04', 1, '2025-04-26 00:09:55.620161-04');

INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'CHANGE_PASSWORD', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'GET_MEMBER_PROFILE', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'GET_FAMILY_DETAILS', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'CHANGE_PASSWORD', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'GET_MEMBER_PROFILE', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'GET_FAMILY_DETAILS', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'CHANGE_PASSWORD', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'GET_MEMBER_PROFILE', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'GET_FAMILY_DETAILS', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'CHANGE_PASSWORD', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'GET_MEMBER_PROFILE', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'GET_FAMILY_DETAILS', 1, '2025-04-26 00:27:48.324267-04', 1, '2025-04-26 00:27:48.324267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'GET_SESSION_DETAILS', 1, '2025-05-25 02:25:50.848267-04', 1, '2025-05-25 02:25:50.848267-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'GET_SESSION_DETAILS', 1, '2025-05-25 02:26:07.067733-04', 1, '2025-05-25 02:26:07.067733-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'GET_SESSION_DETAILS', 1, '2025-05-25 02:26:22.861226-04', 1, '2025-05-25 02:26:22.861226-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'GET_SESSION_DETAILS', 1, '2025-05-25 02:26:39.557435-04', 1, '2025-05-25 02:26:39.557435-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'ADD_FAMILY', 1, '2025-05-30 01:23:56.80583-04', 1, '2025-05-30 01:23:56.80583-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'SEARCH_FAMILY', 1, '2025-07-09 22:37:19.344818-04', 1, '2025-07-09 22:37:19.344818-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'SEARCH_FAMILY', 1, '2025-07-09 22:37:19.344818-04', 1, '2025-07-09 22:37:19.344818-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'SEARCH_FAMILY', 1, '2025-07-09 22:37:19.344818-04', 1, '2025-07-09 22:37:19.344818-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'SEARCH_FAMILY', 1, '2025-07-09 22:37:19.344818-04', 1, '2025-07-09 22:37:19.344818-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'GET_EVENTS', 1, '2025-07-22 21:49:37.371171-04', 1, '2025-07-22 21:49:37.371171-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'GET_EVENTS', 1, '2025-07-22 21:49:37.371171-04', 1, '2025-07-22 21:49:37.371171-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'GET_EVENTS', 1, '2025-07-22 21:49:37.371171-04', 1, '2025-07-22 21:49:37.371171-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'GET_EVENTS', 1, '2025-07-22 21:49:37.371171-04', 1, '2025-07-22 21:49:37.371171-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'GET_ACCOUNT_LIST', 1, '2025-07-25 18:59:24.661079-04', 1, '2025-07-25 18:59:24.661079-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'GET_ACCOUNT_STATEMENT', 1, '2025-07-25 18:59:24.661079-04', 1, '2025-07-25 18:59:24.661079-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'GET_ACCOUNT_LIST', 1, '2025-07-25 18:59:24.661079-04', 1, '2025-07-25 18:59:24.661079-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'GET_ACCOUNT_STATEMENT', 1, '2025-07-25 18:59:24.661079-04', 1, '2025-07-25 18:59:24.661079-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'GET_ACCOUNT_LIST', 1, '2025-07-25 18:59:24.661079-04', 1, '2025-07-25 18:59:24.661079-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'GET_ACCOUNT_STATEMENT', 1, '2025-07-25 18:59:24.661079-04', 1, '2025-07-25 18:59:24.661079-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'GET_ACCOUNT_LIST', 1, '2025-07-25 18:59:24.661079-04', 1, '2025-07-25 18:59:24.661079-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'GET_ACCOUNT_STATEMENT', 1, '2025-07-25 18:59:24.661079-04', 1, '2025-07-25 18:59:24.661079-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'MANAGE_RELATIONSHIP', 1, '2025-08-02 15:35:23.028233-04', 1, '2025-08-02 15:35:23.028233-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'WHO_AM_I', 1, '2025-08-25 05:26:59.195705-04', 1, '2025-08-25 05:26:59.195705-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'WHO_AM_I', 1, '2025-08-25 05:26:59.195705-04', 1, '2025-08-25 05:26:59.195705-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'WHO_AM_I', 1, '2025-08-25 05:26:59.195705-04', 1, '2025-08-25 05:26:59.195705-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'WHO_AM_I', 1, '2025-08-25 05:26:59.195705-04', 1, '2025-08-25 05:26:59.195705-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'UPDATE_MY_FAMILY_IMAGE', 1, '2025-09-08 06:16:22.303736-04', 1, '2025-09-08 06:16:22.303736-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'UPDATE_MY_FAMILY_IMAGE', 1, '2025-09-08 06:16:22.303736-04', 1, '2025-09-08 06:16:22.303736-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'UPDATE_MY_FAMILY_IMAGE', 1, '2025-09-08 06:16:22.303736-04', 1, '2025-09-08 06:16:22.303736-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'UPDATE_ANY_FAMILY_IMAGE', 1, '2025-09-08 06:16:22.303736-04', 1, '2025-09-08 06:16:22.303736-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'UPDATE_MY_FAMILY_IMAGE', 1, '2025-09-08 06:16:22.303736-04', 1, '2025-09-08 06:16:22.303736-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'UPDATE_ANY_FAMILY_IMAGE', 1, '2025-09-08 06:16:22.303736-04', 1, '2025-09-08 06:16:22.303736-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'UPDATE_MY_FAMILY_DETAILS', 1, '2025-09-15 04:44:31.406319-04', 1, '2025-09-15 04:44:31.406319-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'UPDATE_MY_FAMILY_MEMBER', 1, '2025-09-15 04:44:31.406319-04', 1, '2025-09-15 04:44:31.406319-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'UPDATE_MY_FAMILY_DETAILS', 1, '2025-09-15 04:44:31.406319-04', 1, '2025-09-15 04:44:31.406319-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'UPDATE_MY_FAMILY_MEMBER', 1, '2025-09-15 04:44:31.406319-04', 1, '2025-09-15 04:44:31.406319-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'UPDATE_MY_FAMILY_DETAILS', 1, '2025-09-15 04:44:31.406319-04', 1, '2025-09-15 04:44:31.406319-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'UPDATE_MY_FAMILY_MEMBER', 1, '2025-09-15 04:44:31.406319-04', 1, '2025-09-15 04:44:31.406319-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'UPDATE_MY_FAMILY_DETAILS', 1, '2025-09-15 04:44:31.406319-04', 1, '2025-09-15 04:44:31.406319-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'UPDATE_MY_FAMILY_MEMBER', 1, '2025-09-15 04:44:31.406319-04', 1, '2025-09-15 04:44:31.406319-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'UPDATE_MY_FAMILY_MEMBER_IMAGE', 1, '2025-10-05 11:59:10.340529-04', 1, '2025-10-05 11:59:10.340529-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'UPDATE_MY_FAMILY_MEMBER_IMAGE', 1, '2025-10-05 11:59:10.340529-04', 1, '2025-10-05 11:59:10.340529-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'UPDATE_MY_FAMILY_MEMBER_IMAGE', 1, '2025-10-05 11:59:10.340529-04', 1, '2025-10-05 11:59:10.340529-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'UPDATE_MY_FAMILY_MEMBER_IMAGE', 1, '2025-10-05 11:59:10.340529-04', 1, '2025-10-05 11:59:10.340529-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'UPDATE_ANY_FAMILY_DETAILS', 1, '2025-10-14 21:55:32.460867-04', 1, '2025-10-14 21:55:32.460867-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'UPDATE_ANY_FAMILY_MEMBER', 1, '2025-10-14 21:55:32.462768-04', 1, '2025-10-14 21:55:32.462768-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'ADD_MEMBER_TO_ANY_FAMILY', 1, '2025-10-14 21:55:32.464177-04', 1, '2025-10-14 21:55:32.464177-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'ADD_MEMBER_TO_MY_FAMILY', 1, '2025-10-19 10:09:26.72088-04', 1, '2025-10-19 10:09:26.72088-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'SEARCH_FAMILY_MEMBER', 1, '2026-01-06 00:22:03.797608-05', 1, '2026-01-06 00:22:03.797608-05');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'SEARCH_FAMILY_MEMBER', 1, '2026-01-06 00:22:03.799542-05', 1, '2026-01-06 00:22:03.799542-05');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'SEARCH_FAMILY_MEMBER', 1, '2026-01-06 00:22:03.800809-05', 1, '2026-01-06 00:22:03.800809-05');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'SEARCH_FAMILY_MEMBER', 1, '2026-01-06 00:22:03.801959-05', 1, '2026-01-06 00:22:03.801959-05');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'PROCESS_FAMILY_REGISTRATION', 1, '2026-04-04 21:39:00.349033-04', 1, '2026-04-04 21:39:00.349033-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'PROCESS_FAMILY_REGISTRATION', 1, '2026-04-04 21:39:00.350941-04', 1, '2026-04-04 21:39:00.350941-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'GET_FAMILY_COUNT_BY_CITY', 1, '2026-05-25 11:00:21.360713-04', 1, '2026-05-25 11:00:21.360713-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'GET_FAMILY_COUNT_BY_CITY', 1, '2026-05-25 11:00:21.362855-04', 1, '2026-05-25 11:00:21.362855-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'GET_FAMILY_COUNT_BY_CITY', 1, '2026-05-25 11:00:21.364078-04', 1, '2026-05-25 11:00:21.364078-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'GET_FAMILY_COUNT_BY_CITY', 1, '2026-05-25 11:00:21.365694-04', 1, '2026-05-25 11:00:21.365694-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('SUPER_ADMIN_ROLE', 'GET_FAMILIES_BY_REGION', 1, '2026-05-25 11:00:21.367917-04', 1, '2026-05-25 11:00:21.367917-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('APPLICATION_ADMIN_ROLE', 'GET_FAMILIES_BY_REGION', 1, '2026-05-25 11:00:21.368873-04', 1, '2026-05-25 11:00:21.368873-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('FAMILY_ADMIN_ROLE', 'GET_FAMILIES_BY_REGION', 1, '2026-05-25 11:00:21.36995-04', 1, '2026-05-25 11:00:21.36995-04');
INSERT INTO shared_schema.lkproleoperationmap (roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES ('INDIVIDUAL_ROLE', 'GET_FAMILIES_BY_REGION', 1, '2026-05-25 11:00:21.371156-04', 1, '2026-05-25 11:00:21.371156-04');

INSERT INTO shared_schema.memberrelationship (memberid, relationshiptype, relatedmemberid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (66, 'Son', 65, 12, '2025-12-07 13:06:39.145-05', 12, '2025-12-07 13:06:39.145-05');
INSERT INTO shared_schema.memberrelationship (memberid, relationshiptype, relatedmemberid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (66, 'Wife', 67, 12, '2025-12-07 13:10:57.443-05', 12, '2025-12-07 13:10:57.443-05');
INSERT INTO shared_schema.memberrelationship (memberid, relationshiptype, relatedmemberid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (65, 'Wife', 68, 12, '2025-12-09 21:34:01.242-05', 12, '2025-12-09 21:34:01.242-05');
INSERT INTO shared_schema.memberrelationship (memberid, relationshiptype, relatedmemberid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (65, 'Son', 69, 12, '2025-12-09 21:38:25.471-05', 12, '2025-12-09 21:38:25.471-05');
INSERT INTO shared_schema.memberrelationship (memberid, relationshiptype, relatedmemberid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (65, 'Daughter', 70, 12, '2025-12-09 21:40:11.192-05', 12, '2025-12-09 21:40:11.192-05');
INSERT INTO shared_schema.memberrelationship (memberid, relationshiptype, relatedmemberid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (72, 'Wife', 71, 12, '2025-12-09 21:59:18.161-05', 12, '2025-12-09 21:59:18.161-05');
INSERT INTO shared_schema.memberrelationship (memberid, relationshiptype, relatedmemberid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (72, 'Daughter', 73, 12, '2025-12-09 22:01:40.107-05', 12, '2025-12-09 22:01:40.107-05');
INSERT INTO shared_schema.memberrelationship (memberid, relationshiptype, relatedmemberid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (74, 'Wife', 75, 12, '2025-12-10 21:36:55.339-05', 12, '2025-12-10 21:36:55.339-05');
INSERT INTO shared_schema.memberrelationship (memberid, relationshiptype, relatedmemberid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (72, 'Daughter', 68, 1, '2025-12-11 21:51:40.448932-05', 1, '2025-12-11 21:51:40.448932-05');
INSERT INTO shared_schema.memberrelationship (memberid, relationshiptype, relatedmemberid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (66, 'Daughter', 75, 1, '2025-12-11 21:53:41.627766-05', 1, '2025-12-11 21:53:41.627766-05');

INSERT INTO shared_schema.mstuserrolemap (userid, roleid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (12, 'FAMILY_ADMIN_ROLE', 12, '2025-12-07 12:52:19.81-05', 12, '2025-12-07 12:52:19.81-05');
INSERT INTO shared_schema.mstuserrolemap (userid, roleid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (2, 'SUPER_ADMIN_ROLE', 2, '2025-10-12 21:14:42.134-04', 2, '2025-10-12 21:14:42.134-04');
INSERT INTO shared_schema.mstuserrolemap (userid, roleid, createdby, createddate, lastupdatedby, lastupdateddate) VALUES (14, 'FAMILY_ADMIN_ROLE', 14, '2026-02-22 22:35:35.19-05', 14, '2026-02-22 22:35:35.19-05');

INSERT INTO shared_schema.samaj (samajid, samajname, contactdetails) VALUES (1, 'Rajput Chhipa', 'Vijay Garothaya, +1-571-484-3763, vijay_garry@hotmail.com');
INSERT INTO shared_schema.samaj (samajid, samajname, contactdetails) VALUES (2, 'Demo Samaj', 'This samaj was created for demo');
INSERT INTO shared_schema.samaj (samajid, samajname, contactdetails) VALUES (3, 'Temp Samaj', 'This samaj will have a members staged before moving to Rajput Chhipa samaj.');

SELECT pg_catalog.setval('shared_schema.address_addressid_seq', 240, true);

SELECT pg_catalog.setval('shared_schema.appuser_userid_seq', 33, true);

SELECT pg_catalog.setval('shared_schema.family_familyid_seq', 61, true);

SELECT pg_catalog.setval('shared_schema.familyhistory_familyid_seq', 1, false);

SELECT pg_catalog.setval('shared_schema.familyhistory_historyid_seq', 53, true);

SELECT pg_catalog.setval('shared_schema.familymember_memberid_seq', 342, true);

SELECT pg_catalog.setval('shared_schema.familymemberhistory_historyid_seq', 106, true);

SELECT pg_catalog.setval('shared_schema.familymemberregistration_memberrequestid_seq', 162, true);

SELECT pg_catalog.setval('shared_schema.familyregistrationrequest_familyrequestid_seq', 30, true);

SELECT pg_catalog.setval('shared_schema.otpverificationhistory_seqid_seq', 52, true);

SELECT pg_catalog.setval('shared_schema.samaj_samajid_seq', 3, true);

SELECT pg_catalog.setval('shared_schema.txtsession_sessionid_seq', 573, true);

SELECT pg_catalog.setval('shared_schema.txtsessiontxn_txnid_seq', 5329, true);

ALTER TABLE ONLY shared_schema.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (addressid);

ALTER TABLE ONLY shared_schema.appuser
    ADD CONSTRAINT appuser_emailid_key UNIQUE (emailid);

ALTER TABLE ONLY shared_schema.appuser
    ADD CONSTRAINT appuser_logonname_key UNIQUE (logonname);

ALTER TABLE ONLY shared_schema.appuser
    ADD CONSTRAINT appuser_phone_key UNIQUE (phone);

ALTER TABLE ONLY shared_schema.appuser
    ADD CONSTRAINT appuser_pkey PRIMARY KEY (userid);

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_pkey PRIMARY KEY (familyid);

ALTER TABLE ONLY shared_schema.familyhistory
    ADD CONSTRAINT familyhistory_pkey PRIMARY KEY (historyid);

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_email_key UNIQUE (email);

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_logonname_key UNIQUE (logonname);

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_phone_key UNIQUE (phone);

ALTER TABLE ONLY shared_schema.familymember
    ADD CONSTRAINT familymember_pkey PRIMARY KEY (memberid);

ALTER TABLE ONLY shared_schema.familymemberhistory
    ADD CONSTRAINT familymemberhistory_pkey PRIMARY KEY (historyid);

ALTER TABLE ONLY shared_schema.familymemberregistration
    ADD CONSTRAINT familymemberregistration_pkey PRIMARY KEY (memberrequestid);

ALTER TABLE ONLY shared_schema.familyregistrationrequest
    ADD CONSTRAINT familyregistrationrequest_pkey PRIMARY KEY (familyrequestid);

ALTER TABLE ONLY shared_schema.lkpconfig
    ADD CONSTRAINT lkpconfig_pkey PRIMARY KEY (configname, paramname);

ALTER TABLE ONLY shared_schema.lkpoperation
    ADD CONSTRAINT lkpoperation_pkey PRIMARY KEY (operationid);

ALTER TABLE ONLY shared_schema.lkprole
    ADD CONSTRAINT lkprole_pkey PRIMARY KEY (roleid);

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_pkey PRIMARY KEY (roleid, operationid);

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_pkey PRIMARY KEY (memberid, relatedmemberid);

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_pkey PRIMARY KEY (userid, roleid);

ALTER TABLE ONLY shared_schema.otpverification
    ADD CONSTRAINT otpverification_pkey PRIMARY KEY (emailid, otptype);

ALTER TABLE ONLY shared_schema.otpverificationhistory
    ADD CONSTRAINT otpverificationhistory_pkey PRIMARY KEY (seqid);

ALTER TABLE ONLY shared_schema.samaj
    ADD CONSTRAINT samaj_pkey PRIMARY KEY (samajid);

ALTER TABLE ONLY shared_schema.txtsession
    ADD CONSTRAINT txtsession_pkey PRIMARY KEY (sessionid);

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_pkey PRIMARY KEY (txnid);

ALTER TABLE ONLY shared_schema.address
    ADD CONSTRAINT address_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.address
    ADD CONSTRAINT address_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_addressid_fkey FOREIGN KEY (addressid) REFERENCES shared_schema.address(addressid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.family
    ADD CONSTRAINT family_samajid_fkey FOREIGN KEY (samajid) REFERENCES shared_schema.samaj(samajid) ON UPDATE RESTRICT ON DELETE RESTRICT;

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

ALTER TABLE ONLY shared_schema.familymemberregistration
    ADD CONSTRAINT familymemberregistration_familyrequestid_fkey FOREIGN KEY (familyrequestid) REFERENCES shared_schema.familyregistrationrequest(familyrequestid);

ALTER TABLE ONLY shared_schema.lkpconfig
    ADD CONSTRAINT lkpconfig_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkpconfig
    ADD CONSTRAINT lkpconfig_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkpoperation
    ADD CONSTRAINT lkpoperation_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkpoperation
    ADD CONSTRAINT lkpoperation_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkprole
    ADD CONSTRAINT lkprole_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkprole
    ADD CONSTRAINT lkprole_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_operationid_fkey FOREIGN KEY (operationid) REFERENCES shared_schema.lkpoperation(operationid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_roleid_fkey FOREIGN KEY (roleid) REFERENCES shared_schema.lkprole(roleid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_memberid_fkey FOREIGN KEY (memberid) REFERENCES shared_schema.familymember(memberid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_relatedmemberid_fkey FOREIGN KEY (relatedmemberid) REFERENCES shared_schema.familymember(memberid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_roleid_fkey FOREIGN KEY (roleid) REFERENCES shared_schema.lkprole(roleid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_userid_fkey FOREIGN KEY (userid) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.txtsession
    ADD CONSTRAINT txtsession_userid_fkey FOREIGN KEY (userid) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_operationid_fkey FOREIGN KEY (operationid) REFERENCES shared_schema.lkpoperation(operationid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_sessionid_fkey FOREIGN KEY (sessionid) REFERENCES shared_schema.txtsession(sessionid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_userid_fkey FOREIGN KEY (userid) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

GRANT USAGE ON SCHEMA shared_schema TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.address TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.address_addressid_seq TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.appuser TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.appuser_userid_seq TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.family TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.family_familyid_seq TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familyhistory TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familyhistory_familyid_seq TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familyhistory_historyid_seq TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familymember TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familymember_memberid_seq TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familymemberhistory TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familymemberhistory_historyid_seq TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familymemberregistration TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familymemberregistration_memberrequestid_seq TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.familyregistrationrequest TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.familyregistrationrequest_familyrequestid_seq TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.lkpconfig TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.lkpoperation TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.lkprole TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.lkproleoperationmap TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.memberrelationship TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.mstuserrolemap TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.otpverification TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.otpverificationhistory TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.otpverificationhistory_seqid_seq TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.samaj TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.samaj_samajid_seq TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.txtsession TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.txtsession_sessionid_seq TO familytree_app_role;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.txtsessiontxn TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.txtsessiontxn_txnid_seq TO familytree_app_role;

-- PostgreSQL database dump complete

