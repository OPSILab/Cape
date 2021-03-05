# Installing CaPe SDK

This section covers the steps needed to properly install CaPe SDK.

---

CaPe SDK is the software package of CaPe suite that will be provided to a Service Provider (Data Controller).
He can manage several own services with Cape, whose Service descriptions (Service Instance section) will have in common its Business Id (assigned by the running Cape-Server instance).

It will expose a set of APIs (see [CaPe SDK APIs]()) providing integration of all services with the main functionalities provided by Cape, regarding the lifecycle management and storage of:

 
 - Service Signing keys (used during Service Linking phase).
 - Service Link and Service Link Status Records (SLR, SSR) copies issued by Cape after service linking phase and next status updates.
 - Consent and Consent Status Records (CR, CSR) copies issued by Cape after Consenting phase and next status updates.
 - Proof of Possession (PoP) keys issued by Cape during linking phase for Sink services.
 - Authorisation Tokens issued by Cape during consenting phase for Sink services. 
 - Management of Service Linking phases (start, SLR/SSR payloads sign and verification, status updates, etc).
 - Management of Consenting Phase (Consent form generation, CR/CSR payloads verification, Giving consent and its status updates, etc).
 - Managemment of Data Transfer request (Generation and signing of Data request for Sink; Request, Authorisation token and check of associated Consent status for Source, etc).
 
These functionalities will be used both by Data Controller dashboard and by the specific integrated Service Provider's Services. 
For details see [Cape workflow](../workflow/workflow.md) section).

 
**CaPe SDK** is composed by: 

  - **CaPe SDK Client**: Backend part exposing [Cape SDK APIs]() and implemented as Spring Boot Java service, and will be deployed with a tighly coupled storage service (MongoDB).
	   					 Acts as Client by mirroring through its APIs the calls made directly to Cape Server's API. [See installation](install-cape-sdk-client.md).   
 
 - **Cape Angular Frontend Plugin**: Implemented as Angular library to be imported in a Angular project as module. [See installation](install-cape-sdk-angular.md).
