------------------------------------------------------------------------------------------------------------------------
-- connect as postgres (superuser with createrole privilege)
------------------------------------------------------------------------------------------------------------------------
-- Should be idempotent: statements are checked whether execution is necessary when run again
-- duplicating grants is not harmful: PostgreSQL will return a notice but not an error. execution of further statements
-- is not interrrupted
-- BEWARE Check whether the schema drop cascade statement is on or not !!!!
-- the schema drop should be a deliberate action during early development and should not be repeated later
-- without careful backup plan is carried out first
------------------------------------------------------------------------------------------------------------------------

DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT                       -- SELECT list can stay empty for this
      FROM   pg_catalog.pg_roles
      WHERE  rolname = 'spring_security_owner') THEN

      create role spring_security_owner with login encrypted password 'spring_security_owner';
   END IF;

   IF NOT EXISTS (
      SELECT                            -- SELECT list can stay empty for this
      FROM   pg_catalog.pg_roles
      WHERE  rolname = 'spring_security_crud_role') THEN

      create role spring_security_crud_role;    --nologin is default
   END IF;

   IF NOT EXISTS (
      SELECT                            -- SELECT list can stay empty for this
      FROM   pg_catalog.pg_roles
      WHERE  rolname = 'spring_security_read_role') THEN

      create role spring_security_read_role;    --nologin is default
   END IF;

   IF NOT EXISTS (
      SELECT                            -- SELECT list can stay empty for this
      FROM   pg_catalog.pg_roles
      WHERE  rolname = 'spring_security_app') THEN

      create role spring_security_app with login encrypted password 'spring_security_app';
   END IF;
END
$do$;


-- if you duplicate a grant, PostgreSQL will return a notice but not an error.
grant spring_security_crud_role to spring_security_app;

------------------------------------------------------------------------------------------------------------------------
-- BEWARE Dropping the schema and all its content (structures and data) !!!!!!!111
 begin;
 drop schema if exists spring_security_schema cascade;
 commit;
------------------------------------------------------------------------------------------------------------------------

-- if not exists added to be able to run this script whether or not the schema was previously dropped
begin;
create schema if not exists spring_security_schema authorization spring_security_owner;
commit;

-- if you duplicate a grant, PostgreSQL will return a notice but not an error.
grant usage on schema spring_security_schema to spring_security_crud_role;
grant usage on schema spring_security_schema to spring_security_read_role;

-- DO WE ALSO need to grant usage to inducks_app or is that automatically transferred by the previous
-- grant inducks_crud_role to inducks_app;??????

show search_path;
--set search_path to inducks_lib_schema, public;
--show search_path;