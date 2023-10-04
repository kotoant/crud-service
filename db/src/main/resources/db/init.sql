create schema extensions;
grant usage on schema extensions to public;
grant execute on all functions in schema extensions to public;
alter default privileges in schema extensions grant usage on types to public;
create extension pgcrypto schema extensions;

create schema crud_app;
