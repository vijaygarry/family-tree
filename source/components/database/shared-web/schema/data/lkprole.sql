INSERT INTO shared_schema.lkprole(
	roleid, roledesc, enable, 
  createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('INDIVIDUAL_ROLE', 'Generic role for all registered user', true, 
  1, now(), 1, now());

INSERT INTO shared_schema.lkprole(
	roleid, roledesc, enable, 
  createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('FAMILY_ADMIN_ROLE', 'Generic role and manages own family', true, 
  1, now(), 1, now());

INSERT INTO shared_schema.lkprole(
	roleid, roledesc, enable, 
  createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('APPLICATION_ADMIN_ROLE', 'Generic role and manages any family', true, 
  1, now(), 1, now());

  INSERT INTO shared_schema.lkprole(
	roleid, roledesc, enable, 
  createdby, createddate, lastupdatedby, lastupdateddate)
	VALUES ('SUPER_ADMIN_ROLE', 'Super Admin', true, 
  1, now(), 1, now());