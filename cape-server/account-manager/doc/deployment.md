
# Detailed Documentation
- [API documentation](api/)

## Prerequisites

The following tools should be properly installed on your computer:

-   [Git](https://git-scm.com/downloads)
-   [Maven](https://maven.apache.org/download.cgi)
-   [MongoDB 3.3.*](https://www.mongodb.com/download-center#community)

### Proxy configurations

In order to use the different tools behind a proxy please execute the following commands (*username* and *password* are your credential, *proxyhost* is the host name or the IP address of the proxy and *proxyport* is the TCP port of the proxy):

-   **Git**: open a command prompt and execute:

    -   `git config --global http.proxy http://username:password@proxyhost:proxyport`

    -   `git config --global https.proxy http://username:password@proxyhost:proxyport`
    
-   **Maven**: edit the file “*Path\_Of\_Maven/conf/settings.xml*”:
    -   add to the “*&lt;proxies&gt;*” section the proper configuration following the example provided in the same file (please refer to maven guide https://maven.apache.org/guides/mini/guide-proxies.html)

## Create WAR packages

Open a command prompt and Execute the following command to clone the
repository:

-   `git clone <Git repository path>/cape.git`

Move in the specific CaPe module folder

-   `cd cape`

In this folder you will find the CaPe modules subfolders. In order to create all war packages please run the following command:

-   `mvn package`

## Deployment

### Database creation

The Account Manager relies on a MongoDB database to store all the accounts information.
So before deploying the application, it is necessary to create a new database, by importing in the MongoDB server the provided dump file.
- Open a command prompt and Execute the following commands to import the dump file:
    - `cd cape/account-manager`
    - `mongorestore --db accountRepository dump\accountRepository`

> **NOTE**. This dump already contains the statement that creates the **accountsCollection** collection automatically.


### Module WAR deployment
After executing the [Create WAR packages](#create-war-packages) step, all generated war packages will be located inside the module "target" subfolder. Copy the "account-manager.war" artifact to the “webapps” folder of Tomcat installation, start it up and wait until it is deployed.

### Configuration

Once all the WAR files are deployed and the server has started, modify the following configuration files, located in the deployed folders of Tomcat “webapps” folder.

-   **`\account-manager\WEB-INF\classes\application.properties`** change the properties:
    -  **`cape.service.manager.host`** and **`cape.pdata.manager.host`**, with the URL where each module is available. By default, they are the same as the current module: 
            - `http://localhost:8080/service-manager`
            - `http://localhost:8080/pdata-manager`
    - **`cape.db.host`**, **`cape.db.user`**, **`cape.db.password`** with the ones configured during the MongoDB installation. (e.g. for **`cape.db.host`**: `localhost:27017`)

#### Authentication configuration
CaPe supports the User authentication through the **Fiware Identity Manager**, namely **Keyrock**. Both versions 6 and 7 are supported.

##### Configuring CaPe with Fiware IdM Authentication

In order to CaPe correctly execute the OAuth2 flow:

-   CaPe must be registered as an **Application** in the Fiware IdM, by specifying, in the registration form, following parameters:

    -   **URL**: **`http://CAPE_DASHBOARD_HOST`**
    -   **CallbackURL**: **`http://CAPE_DASHBOARD_HOST/login/loginPopup/loginPopup.html`**

> **Note**. Please see the [Fiware Identity Manager](https://fiware-idm.readthedocs.io/en/latest/api/#def-apiOAuth) manual for further information about the registration process, user roles, and **Oauth2** APIs.

##### Configuring CaPe as OAuth2 Client for Fiware IdM authentication

The registration process, described above, provides **`Client Id`** and **`Client Secret`**, which will be used by Idra platform to perform the Oauth2 flow as a Client. Modify the properties of following configuration files, located in the deployed folders of Tomcat `webapps` folder., located in the deployed folders of Tomcat `webapps` folder.

-  **`\account-manager\WEB-INF\classes\application.properties`** change the properties:

     -   **`idm.fiware.version`**: The version of the Fiware IdM, namely Keyrock.
         Allowed values are **6** and **7**.
    -   **`idm.client.id`**: **`Client Id`** provided by the Fiware IdM .
    -   **`idm.client.secret`**: **`Client Secret`** provided by the Fiware IdM.
    -   **`idm.redirecturi`**:**`http://CAPE_DASHBOARD_HOST/login/loginPopup/loginPopup.html`**,
        (same value of the **callbackURL** specified above in the IdM).
    -   **`idm.logout.callback`**: **`http://CAPE_DASHBOARD_HOST`**,
        (same value of the **URL** specified above in the IdM).
    -   **`idm.host`**: Host of Fiware IdM instance. (**INCLUDE ALSO PROTOCOL AND PORT,
        IF ANY**).

> **Note**. Replace **`CAPE_DASHBOARD_HOST`** with the actual value, namely the URL where the CaPe Dashboard is available.




### Server Restart 

In order to apply the previous changes, restart the Tomcat server. Once the server restarted, the module services will be exposed in *http://CAPE_HOST/account-manager*

> **Note**. Change the **`CAPE_HOST`** value with the actual host and port where is exposed the runtime environment

## Support / Contact / Contribution-



## Copying and License

This code is licensed under MIT licence
