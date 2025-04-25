-- Table: shared_schema.lkpconfig

-- DROP TABLE IF EXISTS shared_schema.lkpconfig;

CREATE TABLE IF NOT EXISTS shared_schema.lkpconfig
(
    configname character varying(255) COLLATE pg_catalog."default" NOT NULL,
    paramname character varying(255) COLLATE pg_catalog."default" NOT NULL,
    paramvalue character varying(500) COLLATE pg_catalog."default" NOT NULL,
    enable boolean NOT NULL DEFAULT true,
    listorderseq smallint,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT lkpconfig_pkey PRIMARY KEY (configname, paramname),
    CONSTRAINT lkpconfig_createdby_fkey FOREIGN KEY (createdby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT lkpconfig_lastupdatedby_fkey FOREIGN KEY (lastupdatedby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.lkpconfig
    OWNER to familytree_master;

COMMENT ON COLUMN shared_schema.lkpconfig.configname
    IS 'Main config name e.g. COUNTRYNAME';

COMMENT ON COLUMN shared_schema.lkpconfig.paramname
    IS 'config parameter name e.g. HINDI. This will be the same as config name if no sub config name';

COMMENT ON COLUMN shared_schema.lkpconfig.paramvalue
    IS 'Config value for config name and config param name';

COMMENT ON COLUMN shared_schema.lkpconfig.enable
    IS 'Y means enabled and should be pickup by application';

COMMENT ON COLUMN shared_schema.lkpconfig.listorderseq
    IS 'If config has multiple param, then this will be order of config.';

COMMENT ON COLUMN shared_schema.lkpconfig.createdby
    IS 'User id for user who created this record';

COMMENT ON COLUMN shared_schema.lkpconfig.createddate
    IS 'Time when this record was created';

COMMENT ON COLUMN shared_schema.lkpconfig.lastupdatedby
    IS 'User id for user who last updated this record';

COMMENT ON COLUMN shared_schema.lkpconfig.lastupdateddate
    IS 'Time when this record was last updated';