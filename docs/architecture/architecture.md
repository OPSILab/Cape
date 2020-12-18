# Architecture Overview


CaPe suite is a web platform based on the microservices paradigm.

The following picture illustrates the architecture of Cape.

![alt tag](cape_architecture.png "Cape Architecture")

The architecture is composed by a logical point of view by a set of core backend microservices (Cape Server) and a set of frontend apps (User and Data Controller Dashboard)

Its main components are:

-   ** Cape Server**:
    - **Account Manager**: aaaa
	- **Service Manager**: addd
	- **Consent Manager**: fffff
	- **Auditlog Manager**: 555
	
Each of the components above will be deployed with a tighly coupled storage service.
	
-  ** Cape Dashboards**:
    - **User Dashboard**: aaaa
    - **Data Controller Dashboard**: 	
	
-   **Idra**: is the core of the platform that interacts with federated ODMS
    catalogues; it is responsible for managing internal federation processes. It
    provides the main functionalities through Platform API in order to be
    accessed by external application or by the Idra Portal. Main functionalities
    provided by the FM are: - ODMS catalogues management: registration, removal
    and monitor. - Federated full text search: possibility to search for
    specific Open Data on the federated ODMS catalogues. - Federated queries on
    Linked Open Data. - Federation configuration management

-   **LOD Repository**: is the central store in which collected Linked Open Data
    retrieved from federated ODMS catalogues are stored, in order to perform
    queries on them and to provide collected results in different formats.

-   **Idra Portal**: is a web application that allows end users to access Idra
    functionalities calling the Platform API. In particular, the Idra Portal
    allows to:
    -   Manage administrator authentication
    -   Search for Open Data/Linked Open Data, visualise and manage results
    -   Manage Federation and configuration.


Cape functionalities, such as Service Linking and Consent Management APIs, can be accessed by a generic external Service, issued by a Service Provider registered in Cape as Data Controller.
It can be accomplished by using the **Cape Service SDK**. It is composed by:
   - **Service SDK**: Backend application acting as a client between the Service itslef and Cape platform APIs.
   -- **Service Plugins**: frontend plugins (e.g. Angular package) to be embedded in the Service frontend, in order to communicate with the Service SDK and then with Cape.
