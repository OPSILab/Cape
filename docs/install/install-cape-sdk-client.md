# Installing CaPe SDK Client

This document describes how to install and launch the CaPe SDK Client backend, as done for Cape Server, with two possible methods:

- Building the **WAR packages** from source code.
- Running as **Docker container** with Docker compose (recommended).

---

## Install as WAR package

 After completing the build process, the **cape-service-sdk.war** artefact can be deployed on the Apache Tomcat Server natively installed on host machine.
 
### Prerequisites

In order to build and deploy correctly the package, the following tools should be properly
installed on your computer:


| Name                                                                                                           | Version              | Licence                                 |
| -------------------------------------------------------------------------------------------------------------- | -------------------- |---------------------------------------- |
| [Java OpenJDK](https://openjdk.java.net/)                                                                      | >= 15                 | GNU General Public License Version 2.0  |
| [Apache Tomcat](https://tomcat.apache.org)                                                                     | >=9.0                | Apache License v.2.0                    |
| [MongoDB Community Server](www.mongodb.com)                                                                    | >=4.2.9              | Server Side Public License (SSPL)       |
| [Maven](https://maven.apache.org)                                                                              | >=3.5.0              | Apache License 2.0                      |

&nbsp;
### Build WAR packages

Execute the following commands to create the War packages.

-  Move into `cape-sdk/service-sdk` folder:

```bash
cd cape-sdk/service-sdk
```

- Modify the `pom.xml` file by adding at the root level:

``` 
<packaging>war</packaging> 
```

- Then execute Maven `package` goal:

```bash
mvn package
```

- Into `target` subfolder you should have the **cape-service.sdk.war** package.


### Deployment & Configuration

#### WARs deployment

Move the WAR artifact to the `webapps` folder of Tomcat installation, start
it up and wait until it is deployed.

#### Configuration

Once the WAR file is deployed and the server has started, modify the
properties of `application.properties` configuration file, located in `webapps/cape-service-sdk/WEB-INF/classes/` of Tomcat folder.


**IMPORTANT Note.** Properties are defined following Spring notation **`${ENVVARIABLE_NAME:value}`**. If `ENVVARIABLE_NAME` is defined as environment variable, its value will overwrite 
the default `value`. This is the wanted behaviour in case of [Installation with Docker](#install-with-docker-compose).

Modify properties ("value" part after the colon) in **`cape-service-sdk/WEB-INF/classes/application.properties`**.

  - **`cape.serviceSdk.businessId`**: The **Business Id** assigned to the Service Provider running this instance of Cape SDK. This value must match with the ones set in the Service Provider's Service descriptions. At the moment its value is arbitrary but must be unique within the same Cape Server instance.

  - **`cape.serviceSdk.`**: The full url (`host`:`port`/service-manager) of deployed Cape SDK client artifact (e.g. `http://localhost:8080/cape-service-sdk`).


Following properties will depend on Tomcat installation (typically all the deployed WARs listen on **8080** port and different contexts (e.g /cape-service-sdk/):

 - **`cape.accountManager.host`**: The full url (`host`:`port`/account-manager) of deployed Account Manager WAR (e.g. `http://localhost:8080/account-manager`)
 
 - **`cape.serviceManager.host`**: The full url (`host`:`port`/service-manager) of deployed Service Manager WAR  (e.g. `http://localhost:8080/service-manager`)
 
 - **`cape.serviceRegistry.host`**: The full url (`host`:`port`/service-registry) of deployed Service Registry WAR  (e.g. `http://localhost:8080/service-registry`)
 
 - **`cape.consentManager.host`**: The full url (`host`:`port`/consent-manager) of deployed Account Manager WAR  (e.g. `http://localhost:8080/consent-manager`)


**Note**. Here we suppose that Service SDK Client has been deployed in the same Tomcat of Cape Server installation, otherwise change host and port of **cape.XXX.host** properties accordingly.


Following properties wil depend on MongoDB installation:

 - **`spring.data.mongodb.host`, `spring.data.mongodb.port`**: host and port parameters of the MongoDB server installation (e.g. `localhost` and `27017`).
 - **`spring.data.mongodb.database`**: The database name that will be automatically created and used by the component (Can keep default `accountRepository` value).
 - **`spring.data.mongodb.username`**: MongoDB user
 - **`spring.data.mongodb.password`**: MongoDB password


### IdM Configuration

CaPe Service SDK client will communicate with an Identity Manager acting as Oauth2 Authorization Server (e.g. **Keycloak**)  to:

 - Verify token issued when calling component APIs:
    
    Modify **`spring.security.oauth2.resourceserver.jwt.issuer-uri`** with the JWT Issuer Uri of installed Idm (e.g. `https://IDM_HOST/auth/realms/Cape`)

**Note**. This endpoint will be used to verify token issued for the Oauth2 client application `cape-service-sdk` registered during Idm/Keycloak installation [(see this section)](./index.md#identity-manager).

**Note.** Change **IDM_HOST** with the real hostname where IdM (e.g. Keycloak) has been deployed.

### CORS Configuration

If the Data Controller Dashboard are going to be deployed in a different domain (e.g. http://localhost) than the one of Cape Service SDK client (e.g. https://www.cape-suite.eu), modify one of the following appropriately:

 - **cape.cors.allowed-origin-patterns**
 - **cape.cors.allowed-origins**

in order to correctly enable CORS requests between the Dashboard and Cape Service SDK APIs.

### Applying configuration

In order to apply all the configuration done previously, restart the Tomcat and
wait until the artifact is redeployed.

---
## Install with Docker Compose

This section describes how to install and launch CaPe Server very easily
using [Docker](https://www.docker.com/) and [Docker-Compose](https://docs.docker.com/compose/).

CaPe SDK Client component will be deployed as Docker containers, based on Tomcat Alpine image (service-sdk) and paired with a MongoDB container (service-sdk-mongo).

Default configuration will let run the container in the same Docker network of Cape Server (in order to resolve automatically their hostnames).

Although this can be accomplished by building and running each container, is **recommended** to use directly the provided **docker-compose.yml** file, 
which allows you to start the whole stack by pulling already built images published on [Capesuite Docker Hub repository](https://hub.docker.com/search?q=capesuite&type=image).

CaPe SDK folder provides both **DockerFile** files (in each subfolder) and  the **docker-compose** file, in order to use both methods.

### Prerequisites

In order to correctly build and run Docker containers, you must install of course:

   -  **Docker Engine**: version >= 20.10 ([see the guide](https://docs.docker.com/get-docker/)).
   -  **Docker Compose**: ([see the guide](https://docs.docker.com/compose/install/#install-compose)).

### Start it up with Docker Compose

Docker Compose allows to run the whole stack and to link each component to the other under the same Docker network.

In order to accomplish this:

  - Move into **Cape/cape-sdk/service-sdk** folder
  
  - Before launching docker-compose.yml, modify it to configure environment variables properly, as
    described below.
	
     (**ONLY AFTER COMPLETING FOLLOWING CONFIGURATION SECTION**) 
	 
	 Run the docker-compose file with:

```bash
docker-compose up
```

The containers will be automatically started and attached to the created `cape-network` network (see below).

---
### Configuration

#### Docker networking

The Cape SDK Client container will be attached to the same Docker network of Cape Server and each one will have its own assigned IP and
hostname, internal to the network. In particular, the container hostname will be
equal to the name given in the “services” section of docker-compose file and each will expose its APIs in the published port given in the "ports" section. Thus,
each container can look up the hostname of the others.


**Note.**  As shown below, by default Cape SDK client containers are attached to an external network `cape-server-network`, which is the same created for Cape Server.

```
networks:
 cape-sdk-network:
    external:
      name: cape-server-network
```

In general Cape Server containers and Cape SDK Client would run in different deployment and thus different Docker networks; in that case change the `networks` section with the definition of a new non-external network (e.g. See below).

```
networks:
 cape-sdk-network:
   name: cape-sdk-network
   driver: bridge
   driver_opts:
     com.docker.network.driver.mtu: 1400
   ipam:
     config:
       - subnet: 172.26.1.0/24
```


You can check the created network (after running "docker-compose up"), where all the containers will be attached to,
with:

```bash
docker network ls
```

Once the application was started, you can check IPs assigned to running
containers, with:

```bash
docker inspect network cape-network.
```


**NOTE**.
As the network is a bridge, each port exposed by containers (e.g. 8085), will be
mapped and also reachable in the machine where Docker was installed. Thus, if
the machine is publicly and directly exposed, also these ports will be
reachable, unless they were closed.

#### Configuration through environment variables


**IMPORTANT Note.**.
The properties in each components's `application.properties` file (see [WAR packaging installation](install-cape-server-war.md))
are defined following Spring notation **property=`${ENVVARIABLE_NAME:value}`**. 

Every `ENVVARIABLE_NAME` defined in `environment` sections of each container  will overwrite 
the default `value` defined in the properties file. 

As other configuration (see next sections) relies on docker networking lookup, the only environment variables to be modified is:

 - **`CAPE_IDM_ISSUER_URI`**: with the JWT Issuer Uri of installed Idm (e.g. `https://IDM_HOST/auth/realms/Cape`)


**Note**. This endpoint will be used to verify token issued for the Oauth2 client application `cape-service-sdk` registered during Idm/Keycloak installation [(see this section)](./index.md#identity-manager).

**Note.** Change **IDM_HOST** with the real hostname where IdM (e.g. Keycloak) has been deployed.

### CORS Configuration

If the Data Controller Dashboard and/or Services that will be registered and integrated with Cape SDK are going to be deployed in a different domain (e.g. http://localhost) than the one of Cape Service SDK (e.g. https://www.cape-suite.eu), modify one of the following environment variable appropriately:

  - **CAPE_IDM_ALLOWED_ORIGIN_PATTERNS**
  - **CAPE_IDM_ALLOWED_ORIGINS**

in order to correctly enable CORS requests between the Dashboard and Cape Server APIs.


#### Inter-component communication variables (keep untouched by default)

Following environment variables let to connect a component container to the others. 
Values MUST follow corresponding `ports` configuration on the other containers, by default it is recommended to do not modify them.

Example:

  - **`CAPE_SERVICE_MANAGER_URL`**: default value http://service-manager:8082/service-manager (as service-manager's *ports* section in set with 8082:8080)
  - **`CAPE_ACCOUNT_MANAGER_URL`**: default value http://account-manager:8080/account-manager (as account-manager *ports* section in set with 8081:8080)
  - **`CAPE_SERVICE_REGISTRY_URL`**: default value http://service-registry:8088/service-registry (as service-registry *ports* section in set with 8088:8080)
  - **`CAPE_CONSENT_MANAGER_URL`**: default value http://consent-manager:8083/consent-manager (as consent-manager *ports* section in set with 8083:8080)

**Note.** As described previously, in case of different Docker networks between Cape Server and Cape SDK Client, change properties above accordingly (e.g. change host parts with Gateway IP).

Following properties will depend on paired MongoDB container configuration (keep untouched by default):

Example:

  - **`CAPE_SDK_MONGODB_HOST`**: **service-sdk-mongo** 
  - **`CAPE_SDK_MONGODB_PORT`**: **27017** (as paired services-sdk-mongo container has *ports* section set with 27025:27017, second port value matter)
  - **`CAPE_SDK_MONGODB_USER`**: **root** (as paired services-sdk-mongo container has **`MONGO_INITDB_ROOT_USERNAME`** set with root)
  - **`CAPE_SDK_MONGODB_PWD`**:  **root** (as paired services-sdk-mongo container has **`MONGO_INITDB_ROOT_PASSWORD`** set with **root**)

---
#### Applying configuration

Once all the environment configurations are done, we can run:

```bash
docker-compose up
```

As a result of this command, Cape SDK Client containers will listen on several ports (as defined in each `ports` section).

---
