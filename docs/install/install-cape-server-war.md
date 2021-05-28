# Installing CaPe Server - War packages

This document describes how to install and launch the CaPe Server backend, by building
the WAR packages from source code. After completing the build process, the
following artefacts can be deployed on the Apache Tomcat Server natively installed on host machine:

-   **`account-manager.war`**
-   **`auditlog-manager.war`**
-   **`consent-manager.war`**
-   **`service-manager.war`**
-   **`service-registry.war`**
 
 
### Prerequisites

In order to build and deploy correctly the packages, the following tools should be properly
installed on your computer:


| Name                                                                                                           | Version              | Licence                                 |
| -------------------------------------------------------------------------------------------------------------- | -------------------- |---------------------------------------- |
| [Java OpenJDK](https://openjdk.java.net/)                                                                      | >= 15                 | GNU General Public License Version 2.0  |
| [Apache Tomcat](https://tomcat.apache.org)                                                                     | >=9.0                | Apache License v.2.0                    |
| [MongoDB Community Server](www.mongodb.com)                                                                    | >=4.2.9              | Server Side Public License (SSPL)       |
| [Maven](https://maven.apache.org)                                                                              | >=3.5.0              | Apache License 2.0                      |


---
### Build WAR packages



Execute the following commands to create the War packages.


-  Move into `cape-server` folder:

```bash
cd cape-server
```

- For each component subfolder (e.g. account-manager), modify its `pom.xml` file by adding at the root level:

``` 
<packaging>war</packaging> 
```


- Then execute Maven `package` goal:

```bash
mvn package
```

- Into `target` subfolders of each component (e.g. `cape-server/account-manager`) you should have the corresponding *.war package(e.g. `cape-server/account-manager/target/account-manager.war`)

---

### Deployment & Configuration

### WARs deployment

Move all the WAR artifacts to the `webapps` folder of Tomcat installation, start
it up and wait until they are deployed.

### Configuration

Once all the WAR files are deployed and the server has started, modify the
properties of `application.properties` configuration files, located in each deployed folder of
Tomcat `webapps/XXX/WEB-INF/classes/` folder.


**IMPORTANT Note.** Properties are defined following Spring notation **`${ENVVARIABLE_NAME:value}`**. If `ENVVARIABLE_NAME` is defined as environment variable, its value will overwrite 
the default `value`. This is the wanted behaviour in case of [Installation with Docker](install-cape-server-docker.md).


**Note.** **`cape.XXX.host`** property will have trailing **`/api/v2`** in each application.properties file for that XXX component.

#### Account Manager


Modify properties ("value" part after the colon) in **`cape-server/account-manager/WEB-INF/classes/application.properties`**.

Following properties will depend on Tomcat installation (typically all the deployed WARs listen on **8080** port and different contexts (e.g /account-manager/):

 - **`cape.accountManager.host`**: The full url (`host`:`port`/account-manager/api/v2) of deployed Account Manager WAR (e.g. `http://localhost:8080/account-manager/api/v2`)
 
 - **`cape.serviceManager.host`**: The full url (`host`:`port`/service-manager) of deployed Service Manager WAR  (e.g. `http://localhost:8080/service-manager`)
 
 - **`cape.auditLogManager.host`**: The full url (`host`:`port`/auditlog-manager) of deployed Auditlog Manager WAR  (e.g. `http://localhost:8080/auditlog-manager`)
 
 - **`cape.serviceRegistry.host`**: The full url (`host`:`port`/service-registry) of deployed Service Registry WAR  (e.g. `http://localhost:8080/service-registry`)
 
 - **`cape.consentManager.host`**: The full url (`host`:`port`/consent-manager) of deployed Account Manager WAR  (e.g. `http://localhost:8080/consent-manager`)


Following properties wil depend on MongoDB installation:

 - **`spring.data.mongodb.host`, `spring.data.mongodb.port`**: host and port parameters of the MongoDB server installation (e.g. `localhost` and `27017`).
 - **`spring.data.mongodb.database`**: The database name that will be automatically created and used by the component (Can keep default `accountRepository` value).
 - **`spring.data.mongodb.username`**: MongoDB user
 - **`spring.data.mongodb.password`**: MongoDB password

#### AuditLog Manager


Modify properties ("value" part after the colon) in **`cape-server/auditlog-manager/WEB-INF/classes/application.properties`**.


Following properties will depend on Tomcat installation (typically all the deployed WARs listen on **8080** port and different contexts (e.g /account-manager/):

 - **`cape.auditLogManager.host`**: The full url (`host`:`port`/auditlog-manager/api/v2) of deployed Auditlog Manager WAR  (e.g. `http://localhost:8080/auditlog-manager/api/v2`)
 
 - **`cape.accountManager.host`**: The full url (`host`:`port`/account-manager) of deployed Account Manager WAR (e.g. `http://localhost:8080/account-manager`)
 
 - **`cape.serviceManager.host`**: The full url (`host`:`port`/service-manager) of deployed Service Manager WAR  (e.g. `http://localhost:8080/service-manager`)
 
 - **`cape.serviceRegistry.host`**: The full url (`host`:`port`/service-registry) of deployed Service Registry WAR  (e.g. `http://localhost:8080/service-registry`)


Following properties wil depend on MongoDB installation:

 - **`spring.data.mongodb.host`, `spring.data.mongodb.port`**: host and port parameters of the MongoDB server installation (e.g. `localhost` and `27017`).
 - **`spring.data.mongodb.database`**: The database name that will be automatically created and used by the component (Can keep default `auditlogRepository` value).
 - **`spring.data.mongodb.username`**: MongoDB user
 - **`spring.data.mongodb.password`**: MongoDB password
 
#### Consent Manager


Modify properties ("value" part after the colon) in **`cape-server/consent-manager/WEB-INF/classes/application.properties`**.


Following properties will depend on Tomcat installation (typically all the deployed WARs listen on **8080** port and different contexts (e.g /account-manager/):

 - **`cape.consentManager.host`**: The full url (`host`:`port`/consent-manager/api/v2) of deployed Account Manager WAR  (e.g. `http://localhost:8080/consent-manager/api/v2`)

 - **`cape.accountManager.host`**: The full url (`host`:`port`/account-manager) of deployed Account Manager WAR (e.g. `http://localhost:8080/account-manager`)
 
 - **`cape.serviceManager.host`**: The full url (`host`:`port`/service-manager) of deployed Service Manager WAR  (e.g. `http://localhost:8080/service-manager`) 
 
 - **`cape.auditLogManager.host`**: The full url (`host`:`port`/auditlog-manager) of deployed Auditlog Manager WAR  (e.g. `http://localhost:8080/auditlog-manager`)
  
 - **`cape.serviceRegistry.host`**: The full url (`host`:`port`/service-registry) of deployed Service Registry WAR  (e.g. `http://localhost:8080/service-registry`)
 

Following properties wil depend on MongoDB installation:

 - **`spring.data.mongodb.host`, `spring.data.mongodb.port`**: host and port parameters of the MongoDB server installation (e.g. `localhost` and `27017`).
 - **`spring.data.mongodb.database`**: The database name that will be automatically created and used by the component (Can keep default `consentRepository` value).
 - **`spring.data.mongodb.username`**: MongoDB user
 - **`spring.data.mongodb.password`**: MongoDB password
 

#### Service Manager


Modify properties ("value" part after the colon) in **`cape-server/service-manager/WEB-INF/classes/application.properties`**.


Following properties will depend on Tomcat installation (typically all the deployed WARs listen on **8080** port and different contexts (e.g /account-manager/):

 - **`cape.serviceManager.host`**: The full url (`host`:`port`/service-manager/api/v2) of deployed Service Manager WAR  (e.g. `http://localhost:8080/service-manager/api/v2`)
 
 - **`cape.accountManager.host`**: The full url (`host`:`port`/account-manager) of deployed Account Manager WAR (e.g. `http://localhost:8080/account-manager`)
 
 - **`cape.auditLogManager.host`**: The full url (`host`:`port`/auditlog-manager) of deployed Auditlog Manager WAR  (e.g. `http://localhost:8080/auditlog-manager`)
 
 - **`cape.serviceRegistry.host`**: The full url (`host`:`port`/service-registry) of deployed Service Registry WAR  (e.g. `http://localhost:8080/service-registry`)
 
 - **`cape.consentManager.host`**: The full url (`host`:`port`/consent-manager) of deployed Account Manager WAR  (e.g. `http://localhost:8080/consent-manager`)


Following properties wil depend on MongoDB installation:

 - **`spring.data.mongodb.host`, `spring.data.mongodb.port`**: host and port parameters of the MongoDB server installation (e.g. `localhost` and `27017`).
 
 - **`spring.data.mongodb.database`**: The database name that will be automatically created and used by the component (Can keep default `serviceManagement` value).
 - **`spring.data.mongodb.username`**: MongoDB user
 - **`spring.data.mongodb.password`**: MongoDB password
 

#### Service Registry

Modify properties ("value" part after the colon) in **`cape-server/service-registry/WEB-INF/classes/application.properties`**.


Following properties will depend on Tomcat installation (typically all the deployed WARs listen on **8080** port and different contexts (e.g /account-manager/):

 - **`cape.serviceRegistry.host`**: The full url (`host`:`port`/service-registry/api/v2) of deployed Service Registry WAR  (e.g. `http://localhost:8080/service-registry/api/v2`)


Following properties wil depend on MongoDB installation:

 - **`spring.data.mongodb.host`, `spring.data.mongodb.port`**: host and port parameters of the MongoDB server installation (e.g. `localhost` and `27017`).
 - **`spring.data.mongodb.database`**: The database name that will be automatically created and used by the component (Can keep default `serviceRegistry` value).
 - **`spring.data.mongodb.username`**: MongoDB user
 - **`spring.data.mongodb.password`**: MongoDB password

---
### IdM Configuration

Each component of CaPe Server will communicate with an Identity Manager acting as Oauth2 Authorization Server (e.g. **Keycloak**)  to:

 - Verify token issued when calling component APIs:
    
    Modify **`spring.security.oauth2.resourceserver.jwt.issuer-uri`** with the JWT Issuer Uri of installed Idm (e.g. `https://IDM_HOST/auth/realms/Cape`)

**Note**. This endpoint will be used to verify token issued for the Oauth2 client application `cape-server` registered during Idm/Keycloak installation [(see this section)](./index.md#identity-manager).

**Note.** Change **IDM_HOST** with the real hostname where IdM (e.g. Keycloak) has been deployed.

### CORS Configuration

If the Self Service User Dashboard is going to be deployed in a different domain (e.g. http://localhost) than the one of Cape Server components (e.g. https://www.cape-suite.eu), modify one of the following appropriately:

 - **cape.cors.allowed-origin-patterns**
 - **cape.cors.allowed-origins**

in order to correctly enable CORS requests between the Dashboard and Cape Server APIs.

---
### Applying configuration

In order to apply all the configuration done previously, restart the Tomcat server and
wait until the artifacts are redeployed. 

As a result, Cape Server components will listen on Tomcat endpoint at different contexts.