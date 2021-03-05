# Installing CaPe Data Controller Dashboard

This section covers the steps needed to properly install Cape Data Controller Dashboard. 
It is a Angular portal based on Nebular framework that can be installed in the following ways:

-   Build as Angular distribution and deploy natively on a Web Server
-   Run as Docker containerized environment (recommended)


The following sections describe each installation method in detail.

---
## Install natively on Web Server

Build Angular application and deploy compiled folder on a Web Server.

### Requirements

In order to install Data Controller Dashboard followings must be correctly installed and
configured:

| Framework                                                                                                      | Version                | Licence                                 |
| -------------------------------------------------------------------------------------------------------------- | ---------------------- |---------------------------------------- |
| [NodeJS with NPM](https://nodejs.org/en/) |                                                                    | >=14.15                | MIT                                     |
| [Apache](https://httpd.apache.org) or [Nginx](https://nginx.org/en) Web server                                 | 2.4.43 / 1.18.0        | Apache License v.2.0 /  2-clause BSD    |

&nbsp;
### Build Angular Application

Execute the following commands to create the dist folder.

-  Move into `cape-service-editor/` folder:

```bash
cd cape-service-editor
```

- Run following commands:

```bash
npm install
```

```bash
npm run build:prod
```

- The application files will be compiled into `dist` folder



### Deployment and Configuration

#### Dist folder deployment

Move the files in `dist` subfolder to a new folder on the Web server document root (e.g. `/var/www/html` for Apache and `/usr/share/nginx/html` for Nginx.

#### Configuration

Once the dist folder files are deployed and the server has started, modify the
fields of `config.json` configuration file, located in `dist/assets/` folder.
(These modifications can be made also in `cape-service-editor/src/assets/config.json` file before building the Application, as described in the section above).


- **`serviceRegistry.url`**: with **PUBLIC** (as the Dashboard will make HTTP calls from frontend running locally on browser) endpoint (**`host`:`port`/api/v2**) where Service Registry component of Cape Server is listening:


```
{
  "serviceRegistry": {
	"url": "http://localhost:8088/service-registry/api/v2"
  }
...
```  

- **`system.sdkUrl`**: with **PUBLIC** (as the Dashboard will make HTTP calls from frontend running locally on browser) endpoint (**`host`:`port`/api/v2**) where Cape SDK Client is listening (see [Cape SDK installation](install-cape-sdk-client.md)

```  
  "system": {
    "sdkUrl": "http://localhost:8085/cape-service-sdk/api/v2",
``` 

- **`system.serviceEditorUrl`**: with endpoint (**`host`:`port`**) where Data Controller Dashboard is running (depends on Web server configuration or if running with Docker on different published port).

```   
    "serviceEditorUrl": "http://localhost/cape-service-editor",
```   

- **`i18n.locale`**: with locale (`it`, `en` allowed) enabling internazionalization on Dashboard pages. 

```
  },
  "i18n": {
    "locale": "en" 
  }
}
```

##### IDM Configuration for OAuth2 authentication

CaPe Data Controller Dashboard uses the Oauth2 Implicit flow to perform authentication and authorize to retrieve User information from the configured Idm (whose details, such as email and username will be used to create a Cape Account on first login).

In order to correctly execute the Implicit OAuth2 flow, CaPe Data Controller Dashboard must be registered as an **Application** in the IdM, by
specifying, in the registration form, following parameters:

  -   **Url**: **`http://localhost/cape-service-editor/login`** 
  -   **Callback Url**: **`http://localhost/cape-service-editor/loginPopup`**

**Note**. Replace `localhost` with the real hostname where the Dashboard is deployed.

**Note**. Please see the
[Fiware Identity Manager](https://fiware-idm.readthedocs.io/en/latest/oauth/introduction/index.html)
manual for further information about the registration process and
**Oauth2** APIs.

Change following fields in `config.json` file:

  - **`system.idmHost`** with endpoint (**`host`:`port`**) where IdM (e.g. Keyrock) has been deployed.

``` 
    "idmHost": "https://IDM_HOST",
```	

  - **`system.clientId`** with Client Id provided by application registration in the Idm.

```
    "clientId": "c3b0f7d9-412b-4309-ac70-94a5cb04fcf7",
```

**Note**. Similar configurations and approach can be applied for any other IdM capable of OAuth2 functionalities (e.g. **Keycloak**).

**IMPORTANT Note**. In both installatione modes, will be used `src/assets/config.json` file to configure the portal application.

**SOON** will be used also environment variables in order to ease the configuration in case of installation with Docker.

---
## Install with Docker-compose

Data Controller Dashboard can be run as Docker container (based on Nginx image), by using the provided `docker-compose.yml` file.

### Prerequisites

You must install of course:

   -  **Docker Engine**: version >= 20.10 ([see the guide](https://docs.docker.com/get-docker/)).
   -  **Docker Compose**: ([see the guide](https://docs.docker.com/compose/install/#install-compose)).


### Configuration

The provided `docker-compose.yml` file has also directives to mount the provided `nginx.conf` file, needed to correctly handle deep-linking on deployed Dashboard Angular application.

It contains also the mount to the `src/assets/config.json` file, which allows to configure the Dashboard as described in the [Deployment and Configuration](#deployment-and-configuration) section.

**SOON.** Will be available configuration with environment variables to be set directly in `environment` section of `docker-compose.yml` file.

### Start it up with Docker Compose

Docker Compose allows to run the Docker container by pulling the already built image from [Cape Docker Hub repository]().

In order to accomplish this:

- Move into **`Cape/cape-service-editor`** folder.
  
- Ensure you modified `config.json` file properly, as described in the section above.
	
-  Run the docker-compose file with:

```bash
docker-compose up
```

The containers will be automatically started and attached to the created `cape-network` network.

---
## Launch and Learn

The Data Controller Dashboard is available to the endpoint according to installation mode (Web server or Docker).

Open your favourite browser and point to that endpoint.

Go to [Data Controller Dashboard Manual](../dashboards/data-controller-dashboard/index.md) section to learn how to use the Dashboard.

---

