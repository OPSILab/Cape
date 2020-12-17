# Detailed Documentation
- [Deployment](deployment.md)
- [API documentation](api/)

## Prerequisites

The following tools should be properly installed on your computer:

-   [Git](https://git-scm.com/downloads)
-   [Maven](https://maven.apache.org/download.cgi)
-   [MongoDB 3.3.*](https://www.mongodb.com/download-center#community)

### Proxy configurations

In order to use the different tools behind a proxy please execute the
following commands (*username* and *password* are your credential,
*proxyhost* is the host name or the IP address of the proxy and
*proxyport* is the TCP port of the proxy):

-   **Git**: open a command prompt and execute:

    -   `git config --global http.proxy http://username:password@proxyhost:proxyport`

    -   `git config --global https.proxy http://username:password@proxyhost:proxyport`
    
-   **Maven**: edit the file “*Path\_Of\_Maven/conf/settings.xml*”:
    -   add to the “*&lt;proxies&gt;*” section the proper configuration following the example provided in the same file (please refer to maven guide https://maven.apache.org/guides/mini/guide-proxies.html)

## Create WAR package

Open a command prompt and Execute the following command to clone the
repository:

-   `git clone <Git repository path>/cape.git`

Move in the specific CaPe module folder

-   `cd cape`

In this folder you will find the CaPe modules subfolders. In order to create all war packages please run the following command:

-   `mvn package`



## Deployment

### Database creation

The Service Manager relies on a MongoDB database to store all the service information.

So before deploying the application, it is necessary to create a new database, by importing in the MongoDB server the provided dump file.
- Open a command prompt and Execute the following commands to import the dump file:
    - `cd cape/service-manager`
    - `mongorestore --db personalDataMB dump\personalDataMB`

**NOTE:**
- This dump already contains the statement that creates the **pDataFields** and **serviceRegistry** collection automatically. Same service description are already stored. A Service Description sample is provided in [service-descriptions](service-descriptions/). 

### Module WAR deployment
After executing the [Create WAR packages](#create-war-package) step, all generated war packages will be located inside the module "target" subfolder. Copy the "account-manager.war" artifact to the “webapps” folder of Tomcat installation, start it up and wait until it is deployed.

### Configuration

Once all the WAR files are deployed and the server has started, modify
the following configuration files, located in the deployed folders of
Tomcat “webapps” folder.

-   **\service-manager\WEB-INF\classes\application.properties** change the properties:
   - **`cape.db.host`**, **`cape.db.user`**, **`cape.db.password`** with the ones configured during the MongoDB installation. (e.g. for **`cape.db.host`**: `localhost:27017`)



### Server Restart


In order to apply the previous changes, restart the Tomcat server. Once
the server restarted, the module services will be exposed in
*http://CAPE_HOST/service-manager*
A new service description can be stored and managed by CRUD operations (POST/GET/PUT/DELETE) (*http://BASEPATH/service-manager/api/v1/services*)

**Note**. Change the CAPE_HOST value with the actual host and port
where is exposed the runtime environment

## Support / Contact / Contribution-

[*vincenzo.savarino@eng.it*](mailto:vincenzo.savarino@eng.it)

## Copying and License

This code is licensed under MIT licence





