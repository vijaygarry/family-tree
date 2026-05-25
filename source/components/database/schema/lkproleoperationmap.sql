CREATE TABLE shared_schema.lkproleoperationmap (
    roleid character varying(55) NOT NULL,
    operationid character varying(55) NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);

ALTER TABLE shared_schema.lkproleoperationmap OWNER TO familytree_master;

COMMENT ON TABLE shared_schema.lkproleoperationmap IS 'Role Operation link table. This will have list of operation mapped to a role.';

COMMENT ON COLUMN shared_schema.lkproleoperationmap.roleid IS 'Role Id';

COMMENT ON COLUMN shared_schema.lkproleoperationmap.operationid IS 'Operation id assign to role';

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_pkey PRIMARY KEY (roleid, operationid);

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_operationid_fkey FOREIGN KEY (operationid) REFERENCES shared_schema.lkpoperation(operationid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.lkproleoperationmap
    ADD CONSTRAINT lkproleoperationmap_roleid_fkey FOREIGN KEY (roleid) REFERENCES shared_schema.lkprole(roleid) ON UPDATE RESTRICT ON DELETE RESTRICT;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.lkproleoperationmap TO familytree_app_role;
