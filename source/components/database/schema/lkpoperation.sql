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

COMMENT ON TABLE shared_schema.lkpoperation IS 'Main operation table which mapped to one API call to application.';

COMMENT ON COLUMN shared_schema.lkpoperation.operationid IS 'Unique alpha numeric opearation id';

COMMENT ON COLUMN shared_schema.lkpoperation.description IS 'Operation description';

COMMENT ON COLUMN shared_schema.lkpoperation.beanname IS 'Bean name/class name to invoke for this operation';

COMMENT ON COLUMN shared_schema.lkpoperation.isauthorizationrequired IS 'True indicate user authentication/authorization is required to execute this operation.';

COMMENT ON COLUMN shared_schema.lkpoperation.isauditrequired IS 'True indicate audit this transaction';

COMMENT ON COLUMN shared_schema.lkpoperation.authorizationtype IS 'Type of authorization required for this operation. Possible values are NO_AUTHORIZATION, IP_BASE, ROLE_BASE';

COMMENT ON COLUMN shared_schema.lkpoperation.active IS 'If this value is true, then application will load this operation, if this flag is false, then application won''t recognize this operation.';

COMMENT ON COLUMN shared_schema.lkpoperation.createdby IS 'User id for user who created this record';

COMMENT ON COLUMN shared_schema.lkpoperation.createddate IS 'Time when this record was created';

COMMENT ON COLUMN shared_schema.lkpoperation.lastupdatedby IS 'User id for user who last updated this record';

COMMENT ON COLUMN shared_schema.lkpoperation.lastupdateddate IS 'Time when this record was last updated';

ALTER TABLE ONLY shared_schema.lkpoperation
    ADD CONSTRAINT lkpoperation_pkey PRIMARY KEY (operationid);

ALTER TABLE ONLY shared_schema.lkpoperation
    ADD CONSTRAINT lkpoperation_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkpoperation
    ADD CONSTRAINT lkpoperation_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.lkpoperation TO familytree_app_role;
