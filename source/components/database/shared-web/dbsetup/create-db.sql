-- Application database creation
\set databaseName 'family_tree'
\set schemaName 'web_infra'

CREATE DATABASE family_tree
    WITH
    OWNER = familytree_master
    ENCODING = 'UTF8';

COMMENT ON DATABASE family_tree
    IS 'Family Tree database';

ALTER DATABASE family_tree SET search_path TO web_infra;
