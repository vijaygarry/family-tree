-- Table: shared_schema.mstuserrolemap

-- DROP TABLE IF EXISTS shared_schema.mstuserrolemap;

CREATE TABLE IF NOT EXISTS shared_schema.mstuserrolemap
(
    userid integer NOT NULL,
    roleid character varying(100) COLLATE pg_catalog."default" NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT mstuserrolemap_pkey PRIMARY KEY (userid, roleid),
    CONSTRAINT mstuserrolemap_createdby_fkey FOREIGN KEY (createdby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT mstuserrolemap_lastupdatedby_fkey FOREIGN KEY (lastupdatedby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT mstuserrolemap_roleid_fkey FOREIGN KEY (roleid)
        REFERENCES shared_schema.lkprole (roleid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT mstuserrolemap_userid_fkey FOREIGN KEY (userid)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.mstuserrolemap
    OWNER to familytree_master;

COMMENT ON TABLE shared_schema.mstuserrolemap
    IS 'User role link table. This table will have list of roles assign to user.';

COMMENT ON COLUMN shared_schema.mstuserrolemap.userid
    IS 'User Id';

COMMENT ON COLUMN shared_schema.mstuserrolemap.roleid
    IS 'Role Id';