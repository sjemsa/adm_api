Project created with Spring initializer: https://start.spring.io/
with following dependancies:
* Oracle Driver for SQL
* Flyway Migration for install
* Spring Data JPA to persist data in SQL stores with Java Persistance API
* Spring Web to build a REST api

For database development used Oracle database and I used my current work virtual test environment database for testing!

Swagger used as an ui and ran locally: http://localhost:8081/swagger-ui/index.html#

Application consist of 3 main classes:
* Controller (AdmApi.java) to locate the endpoint
* Service (AdmService.java) to store business logic(as all business logic is in plsql file is used as buffer)
* Repository (AdmDatabaseRepository) for communication to database and mapping database objects


Before deploying database create a database user with following scripts:

User creation script:

create tablespace dm_tab datafile size 100M autoextend on next 50M maxsize 10G extent management local segment space management auto;

create tablespace dm_ind datafile size 100M autoextend on next 50M maxsize 10G extent management local segment space management auto;

create user decision_maker identified by decision_maker default tablespace users temporary tablespace temp;

grant create session, resource to decision_maker;

alter user decision_maker default tablespace dm_tab;

alter user decision_maker quota unlimited on dm_tab;

alter user decision_maker quota unlimited on dm_ind;


In current project is currently missing:
* datasource URL in application.properties file. 
* pluginManagement in settings.gradle in needed.

Data type specific solutions: 
* i didn't know if engine is gonna be used for only local citizens or any so no standartization checks are implemented for personal code
* period and amount are set as BigDecimal for reason plsql number type didnt like int:)

Test scripts were written but not executed once because i didnt have an access to database last 2 days where tests were written.  