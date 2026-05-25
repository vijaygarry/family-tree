CREATE TABLE shared_schema.mstuserrolemap (
    userid integer NOT NULL,
    roleid character varying(100) NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);

ALTER TABLE shared_schema.mstuserrolemap OWNER TO familytree_master;

COMMENT ON TABLE shared_schema.mstuserrolemap IS 'User role link table. This table will have list of roles assign to user.';

COMMENT ON COLUMN shared_schema.mstuserrolemap.userid IS 'User Id';

COMMENT ON COLUMN shared_schema.mstuserrolemap.roleid IS 'Role Id';

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_pkey PRIMARY KEY (userid, roleid);

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_roleid_fkey FOREIGN KEY (roleid) REFERENCES shared_schema.lkprole(roleid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.mstuserrolemap
    ADD CONSTRAINT mstuserrolemap_userid_fkey FOREIGN KEY (userid) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.mstuserrolemap TO familytree_app_role;
