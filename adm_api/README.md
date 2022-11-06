Please configure datasource URL in application.properties file.
I used my work virtual test environment database for testing!

Before deploying database create a database user with following scripts:

-----------------------User creation script-------------------------

create tablespace dm_tab datafile size 100M autoextend on next 50M maxsize 10G extent management local segment space management auto;

create tablespace dm_ind datafile size 100M autoextend on next 50M maxsize 10G extent management local segment space management auto;

create user decision_maker identified by decision_maker default tablespace users temporary tablespace temp;

grant create session, resource to decision_maker;

alter user decision_maker default tablespace dm_tab;

alter user decision_maker quota unlimited on dm_tab;

alter user decision_maker quota unlimited on dm_ind;


-------------------------------------------------------------------------

Swagger could be ran locally: http://localhost:8081/swagger-ui/index.html#

-------------------------------------------------------------------------


Configure spring.datasource.url in application.properties
and pluginManagement in settings.gradle
