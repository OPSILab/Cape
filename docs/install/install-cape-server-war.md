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
| [Java OpenJDK](https://openjdk.java.net/)                                                                      | >= 8                 | GNU General Public License Version 2.0  |
| [Apache Tomcat](https://tomcat.apache.org)                                                                     | >=8.5                | Apache License v.2.0                    |
| [MongoDB Community Server](www.mongodb.com)                                                                    | >=4.0.9              | Server Side Public License (SSPL)       |
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

CaPe Server will communicate with Fiware Keyrock Identity Manager (or any other OAuth2 capable IdM, e.g. **Keycloak**)  to:

 - Verify token issued when calling component APIs:
    
    Modify **`security.oauth2.resource.user-info-uri`** with Idm User Info URI (default for Keyrock: `http(s)://IDM_HOST:3000/user`)

 - Perform the "Client credentials" OAuth2 authorization flow through the IdM 
   Tipically not used since CaPe Dashboards will use by default the Implicit flow directly from frontend.
   In specific case (e.g. SPID and EIDAS login) CaPe Dashboards could use Client Credentials grant.
   
    See below and the [Account Manager APIs]() for further information.



##### Configure CaPe for Client Credentials flow (optional)

In order to correctly execute the Client Credentials OAuth2 flow CaPe Server must be registered as an **Application** in the Fiware IdM, by
specifying, in the registration form, following parameters (considering as example Cape User Self-Service Dashboard running on localhost):

  -   **Url**: **`http://localhost/cape-dashboard/login`** 
  -   **Callback Url**: **`http://localhost/cape-dashboard/loginPopup`**


**Note**. Please see the
[Fiware Identity Manager](https://fiware-idm.readthedocs.io/en/latest/oauth/introduction/index.html)
manual for further information about the registration process and
**Oauth2** APIs.



The registration process, described above, provides **`Client Id`** and **`Client Secret`**, which will be used by Cape to perform the Oauth2 flow as a Client.

Modify following properties (only in Account Manager configuration, since it implements the [APIs]() to perform Oauth "authorize" call:

  - **idm.host**: (default for Keyrock: `http://IDM_HOST:3000`)
  - **idm.clientId**, **idm.clientSecret**: Client Id and Client Secret provided by application registration in the Idm.
  

**Note.** Change **IDM_HOST** with the real hostname where IdM (e.g. Keyrock) has been deployed.

**Note**. Similar configurations and approach can be applied for any other IdM exposing OAuth2 functionalities (e.g Keycloak).

---
### Applying configuration

In order to apply all the configuration done previously, restart the Tomcat and
wait until the artifacts are redeployed. 

As a result, Cape Server components will listen on Tomcat endpoint at different contexts.