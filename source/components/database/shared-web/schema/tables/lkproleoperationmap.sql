-- Table: shared_schema.lkproleoperationmap

-- DROP TABLE IF EXISTS shared_schema.lkproleoperationmap;

CREATE TABLE IF NOT EXISTS shared_schema.lkproleoperationmap
(
    roleid character varying(55) COLLATE pg_catalog."default" NOT NULL,
    operationid character varying(55) COLLATE pg_catalog."default" NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT lkproleoperationmap_pkey PRIMARY KEY (roleid, operationid),
    CONSTRAINT lkproleoperationmap_createdby_fkey FOREIGN KEY (createdby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT lkproleoperationmap_lastupdatedby_fkey FOREIGN KEY (lastupdatedby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT lkproleoperationmap_operationid_fkey FOREIGN KEY (operationid)
        REFERENCES shared_schema.lkpoperation (operationid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT lkproleoperationmap_roleid_fkey FOREIGN KEY (roleid)
        REFERENCES shared_schema.lkprole (roleid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.lkproleoperationmap
    OWNER to familytree_master;

COMMENT ON TABLE shared_schema.lkproleoperationmap
    IS 'Role Operation link table. This will have list of operation mapped to a role.';

COMMENT ON COLUMN shared_schema.lkproleoperationmap.roleid
    IS 'Role Id';

COMMENT ON COLUMN shared_schema.lkproleoperationmap.operationid
    IS 'Operation id assign to role';