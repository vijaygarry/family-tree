\qecho 'creating database users for familytree application.'

-- Create familytree_master user to manage database. Application won't use this user.
CREATE USER familytree_master WITH PASSWORD 'MasterPassword123' ;

-- Create user for application.
CREATE USER familytree_app_user WITH PASSWORD 'AppUser123';

-- Create user for replication. This user will be used only for replication.
CREATE USER replicator REPLICATION PASSWORD 'replicator';

-- Create role. All permissions will be given to this role.
-- Application user will belongs to this role.
CREATE ROLE familytree_app_role
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
COMMENT ON ROLE familytree_app_role IS 'Group for family tree app user';

-- grant app group to app user
grant familytree_app_role to familytree_app_user;