-- Table: shared_schema.lkprole

-- DROP TABLE IF EXISTS shared_schema.lkprole;

CREATE TABLE IF NOT EXISTS shared_schema.lkprole
(
    roleid character varying(55) COLLATE pg_catalog."default" NOT NULL,
    roledesc character varying(255) COLLATE pg_catalog."default" NOT NULL,
    enable boolean NOT NULL DEFAULT true,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT lkprole_pkey PRIMARY KEY (roleid),
    CONSTRAINT lkprole_createdby_fkey FOREIGN KEY (createdby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT lkprole_lastupdatedby_fkey FOREIGN KEY (lastupdatedby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.lkprole
    OWNER to familytree_master;

COMMENT ON TABLE shared_schema.lkprole
    IS 'Role table';

COMMENT ON COLUMN shared_schema.lkprole.roleid
    IS 'Unique alphanumeric role id';

COMMENT ON COLUMN shared_schema.lkprole.roledesc
    IS 'Desc for this role';

COMMENT ON COLUMN shared_schema.lkprole.enable
    IS 'This flag indicate if this role is active.';