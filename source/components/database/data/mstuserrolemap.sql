/*
Query to generate insert query. Replace roleId for user
select 'INSERT INTO shared_schema.mstuserrolemap(' ||
chr(10) || '	userid, roleid, createdby, createddate, lastupdatedby, lastupdateddate)' ||
chr(10) || '	VALUES (''' || userid || ''', ''INDIVIDUAL_ROLE'', 1, now(), 1, now());' 
from shared_schema.appuser
where status = 'A';
*/

INSERT INTO shared_schema.mstuserrolemap(
	userid, roleid, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('2', 'SUPER_ADMIN_ROLE', 1, now(), 1, now());