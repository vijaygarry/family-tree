INSERT INTO shared_schema.lkpoperation(
	operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, 
    active, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('LOGIN', 'Login to application - authenticate user and create session', 'UserLogin', false, true, 'NO_AUTHORIZATION', 
    true, 1, now(), 1, now());

INSERT INTO shared_schema.lkpoperation(
	operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, 
    active, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('LOGOUT', 'Logout User - Destroy the current session', 'UserLogout', false, true, 'NO_AUTHORIZATION', 
    true, 1, now(), 1, now());

INSERT INTO shared_schema.lkpoperation(
	operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, 
    active, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('GET_SESSION_DETAILS', 'Get session details', 'GetUserSessionDetails', true, true, 'ROLE_BASE', 
    true, 1, now(), 1, now());

INSERT INTO shared_schema.lkpoperation(
	operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, 
    active, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('IS_SESSION_VALID', 'Is session valid', 'IsUserSessionValid', false, true, 'NO_AUTHORIZATION', 
    true, 1, now(), 1, now());

INSERT INTO shared_schema.lkpoperation(
	operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, 
    active, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('CHANGE_PASSWORD', 'Change user password', 'ChangeUserPassword', true, true, 'ROLE_BASE', 
    true, 1, now(), 1, now());

INSERT INTO shared_schema.lkpoperation(
	operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, 
    active, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('GET_MEMBER_PROFILE', 'Get family member profile', 'GetFamilyMemberProfile', true, true, 'ROLE_BASE', 
    true, 1, now(), 1, now());


INSERT INTO shared_schema.lkpoperation(
	operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, 
    active, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('GET_FAMILY_DETAILS', 'Get family details', 'GetFamilyDetails', true, true, 'ROLE_BASE', 
    true, 1, now(), 1, now());

INSERT INTO shared_schema.lkpoperation(
	operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, 
    active, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('SEARCH_FAMILY', 'Search family', 'SearchFamilyOperation', true, true, 'ROLE_BASE', 
    true, 1, now(), 1, now());
    
INSERT INTO shared_schema.lkpoperation(
	operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, 
    active, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('ADD_FAMILY', 'Add Family', 'AddFamilyBean', true, true, 'ROLE_BASE', 
    true, 1, now(), 1, now());


INSERT INTO shared_schema.lkpoperation(
	operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, 
    active, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('ADD_FAMILY_MEMBER', 'Add Family Member', 'AddFamilyMemberBean', true, true, 'ROLE_BASE', 
    true, 1, now(), 1, now());

INSERT INTO shared_schema.lkpoperation(
	operationid, description, beanname, isauthorizationrequired, isauditrequired, authorizationtype, 
    active, createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('GET_EVENTS', 'Get events', 'GetEvents', true, true, 'ROLE_BASE', 
    true, 1, now(), 1, now());

-- Get Family stats
-- Total number of families, total number of members.
-- Registered users
-- Number of family registered per region
-- Number of users registered per region

-- Event details:
-- Past events
-- Upcoming events

-- Accounts:
-- Account summary etc.