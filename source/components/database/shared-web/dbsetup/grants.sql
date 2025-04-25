REVOKE ALL ON SCHEMA app_schema FROM PUBLIC;
REVOKE ALL ON SCHEMA app_schema FROM familytree_master;

GRANT ALL ON SCHEMA app_schema TO familytree_master;
-- grant access on app_schema schema to app group
GRANT USAGE ON SCHEMA app_schema TO familytree_app_role;


GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA app_schema TO familytree_app_role;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA app_schema TO familytree_app_role;
GRANT ALL ON ALL SEQUENCES IN SCHEMA app_schema TO familytree_app_role;

REVOKE ALL ON SCHEMA shared_schema FROM PUBLIC;
REVOKE ALL ON SCHEMA shared_schema FROM familytree_master;

GRANT ALL ON SCHEMA shared_schema TO familytree_master;
-- grant access on app_schema schema to app group
GRANT USAGE ON SCHEMA shared_schema TO familytree_app_role;


GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA shared_schema TO familytree_app_role;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA shared_schema TO familytree_app_role;
GRANT ALL ON ALL SEQUENCES IN SCHEMA shared_schema TO familytree_app_role;
