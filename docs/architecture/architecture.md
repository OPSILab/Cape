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
	
	
Cape functionalities, such as Service Linking and Consent Management APIs, can be accessed by a generic external Service, issued by a Service Provider registered in Cape as Data Controller.
It can be accomplished by using the **Cape Service SDK**. It is composed by:
   - **Service SDK**: Backend application acting as a client between the Service itslef and Cape platform APIs.
   -- **Service Plugins**: frontend plugins (e.g. Angular package) to be embedded in the Service frontend, in order to communicate with the Service SDK and then with Cape.
