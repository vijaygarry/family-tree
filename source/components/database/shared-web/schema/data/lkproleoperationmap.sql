/*
Query to generate insert query. Replace roleId to generate script for each role:
select 'INSERT INTO shared_schema.lkproleoperationmap(' || 
chr(10) || '	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)' || 
chr(10) || 'VALUES (''INDIVIDUAL_ROLE'', ''' || operationid || ''', 1, now(), 1, now());' 
from shared_schema.lkpoperation
where isauthorizationrequired = true;

Query to generate inserts for all roles. Need to remove few operations manually for selected role:
select 'INSERT INTO shared_schema.lkproleoperationmap(' || 
chr(10) || '	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)' || 
chr(10) || 'VALUES (''' || r.roleid || ''', ''' || o.operationid || ''', 1, now(), 1, now());'
from shared_schema.lkpoperation o, shared_schema.lkprole r
where o.isauthorizationrequired = true;

*/

INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('INDIVIDUAL_ROLE', 'CHANGE_PASSWORD', 1, now(), 1, now());
INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('INDIVIDUAL_ROLE', 'GET_MEMBER_PROFILE', 1, now(), 1, now());
INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('INDIVIDUAL_ROLE', 'GET_FAMILY_DETAILS', 1, now(), 1, now());

INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('FAMILY_ADMIN_ROLE', 'CHANGE_PASSWORD', 1, now(), 1, now());
INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('FAMILY_ADMIN_ROLE', 'GET_MEMBER_PROFILE', 1, now(), 1, now());
INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('FAMILY_ADMIN_ROLE', 'GET_FAMILY_DETAILS', 1, now(), 1, now());

INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('APPLICATION_ADMIN_ROLE', 'CHANGE_PASSWORD', 1, now(), 1, now());
INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('APPLICATION_ADMIN_ROLE', 'GET_MEMBER_PROFILE', 1, now(), 1, now());
INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('APPLICATION_ADMIN_ROLE', 'GET_FAMILY_DETAILS', 1, now(), 1, now());

INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('SUPER_ADMIN_ROLE', 'CHANGE_PASSWORD', 1, now(), 1, now());
INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('SUPER_ADMIN_ROLE', 'GET_MEMBER_PROFILE', 1, now(), 1, now());
INSERT INTO shared_schema.lkproleoperationmap(
	roleid, operationid, createdby, createddate, lastupdatedby, lastupdateddate)
VALUES ('SUPER_ADMIN_ROLE', 'GET_FAMILY_DETAILS', 1, now(), 1, now());