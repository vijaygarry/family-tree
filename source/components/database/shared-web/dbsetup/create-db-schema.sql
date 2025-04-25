\qecho 'creating family_tree db'

-- Switch to family_tree database 
\connect family_tree

-- Drop the default public schema
DROP schema public;

\qecho 'creating app_schema in family_tree db'
-- Create application specific schema
CREATE SCHEMA app_schema AUTHORIZATION familytree_master;
COMMENT ON SCHEMA app_schema
    IS 'Scehma specific to family tree application';


\qecho 'creating shared_auth in family_tree db'
-- Create application specific schema
CREATE SCHEMA shared_schema AUTHORIZATION familytree_master;
COMMENT ON SCHEMA shared_schema
    IS 'Shared schema which includes tables related to web infra.';
