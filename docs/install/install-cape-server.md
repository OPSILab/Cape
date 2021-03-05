# Installing CaPe Server

This section covers the steps needed to properly install CaPe Server.

---

CaPe Server is the core backend of the CaPe platform. 
It implements and exposes all the main functionalities provided by Cape, regarding the lifecycle management and storage of Service Descriptions, Service Linking, Consent Records and Auditing (see [Cape workflow](../workflow/workflow.md) section).

Its main components are: 

 - **Account Manager**: Manage the lifecycle of the Cape Account, Account signing keys for Service Linking. 
 - **Service Manager**: Manage the Service Linking internal processes and Service Link Record storage.
 - **Consent Manager**: Manage the Consent Records lifecycle, the generation of Consent Forms, etc.
 - **Auditlog Manager**: Collects aggregated auditing statistics, triggered by incoming Event Logs (ServiceLink, Consent or Data Processing) regarding a specific Account.
 - **Service Registry** : Collects the Service Descriptions and registrations (Signing keys and certificates).

Each of them are implemented as Spring Boot Java services, and will be deployed with a tighly coupled storage service (MongoDB).

Two instalaltion modes are available:

-   [Install natively as War Package](install-cape-server-war.md) (needs packaging type adjustements in the POM files).
-   [Install with Docker](install-cape-server-docker.md) (Recommended)