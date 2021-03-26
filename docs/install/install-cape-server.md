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

Two installation modes are available:

-   [Install natively as War Package](install-cape-server-war.md) (needs packaging type adjustements in the POM files).
-   [Install with Docker](install-cape-server-docker.md) (Recommended)

---
## After installing Cape Server

Once you have installed Cape Server, you must follow following steps to start using its APIs directly, through Dashboards or through Cape SDK.

 - Create Operator Description
 
---
### Create Operator Description
In particular, you have to create the **OperatorDescription**, which will describe the installed instance of Cape Server.

**NOTE**. This Operator (intended according to the My Data specification) is not to be confused with the Service Provider's operator that will interact with Cape by the means of Data Controller Dashboard and Cape SDK/APIs.

#### Operator Description model

Following table describes the **Operator Description** model:

| Property Name                     | Type         | Description                                                 | Allowed values                                                                      |
|-----------------------------------|--------------|-------------------------------------------------------------|-------------------------------------------------------------------------------------|
| operatorId                        | string       | Unique id for the Operator (arbitrary)                      | any                                                                                 |
| serviceProvider                   | Service Provider       | Object describing Cape's Service Provider                   | See below                                                                           |
| operatorServiceDescriptionVersion | string       | Operator Description version                                | any                                                                                 |
| supportedProfiles                 | enum []      | Supported profiles                                          | "contract", "consenting",​ "​3rd​ ​party​ ​re-use",​ "​notification",​ "​objection" |
| operatorUrls                      | OperatorUrls | Object describing Operator Url used in Cape functionalities | See below                                                                           |


Following table describes the **Service Provider** model (intended as **Cape Provider**):

| Property Name | Type   | Description                                     |
|---------------|--------|-------------------------------------------------|
| businessId    | string | Unique identifier (arbitrary) for Cape Provider |
| name          | string | Cape Provider name                              |
| address1      | string | Principal address                               |
| address2      | string | Secondary address                               |
| postalcode    | string | Postal Code                                     |
| city          | string | City                                            |
| state         | string | State                                           |
| country       | string | Country                                         |
| email         | string | Email                                           |
| phone         | string | Phone number                                    |
| jurisdiction  | string | Jurisdiction                                    |



Following table describes the fields of **OperatorUrls** class:			
These Urls depends on how the Cape SDK is installed,

| Property Name      | Type   | Description                                                         |
|--------------------|--------|---------------------------------------------------------------------|
| domain             | string | Cape Server's domain BASE URL (http(s)://host:port) . E.g. ``               |
| linkingUri         | string | URL user should be redirected when Service Linking started from the Service (must be `http(s)://CAPE_DASHBOARD_URL/serviceLinking`) |
| linkingRedirectURI | string | URL​ ​user​ ​should​ ​be redirected​ ​after​ ​service​ ​has been​ ​linked.​ ​Used​ ​in​ ​Service Linking​ ​starting​ ​from​ User Self-Service Dashboard  (must be `http(s)://CAPE_DASHBOARD_URL/pages/services/linkedServices)` |

---

**NOTE**. Replace **CAPE_DASHBOARD_URL** with the actual Base url (reachable by browser) where User Self-Service dashboard was installed.
          Consider that User Self Service Dashboard by default appends `/cape-dashboard` to the Base url where it is public accessible (e.g. `http://localhost`), see the example below.

**NOTE**. The **businessId** here is related to the Provider of Cape Suite itself (e.g. Engineering Ingegneria Informatica S.p.A) not to be confused with the businessId relative to the **Service Provider/Data Controller**, which will be used in the Cape SDK configuration and related Service Descriptions.



#### Create Operator Description via API

In order to create the Operator Description on Cape Server, issue a POST with a JSON body like the following one to the `/api/v2/operatorDescriptions` endpoint of Cape Server's Service Manager component (depending on how you have installed Cape Server. E.g. `http://localhost:8082/service-manager/api/v2/operatorDescriptions`).

```
{
  "operatorId": "cape",
  "serviceProvider": {
    "businessId": "ENG", 
    "name": "string",
    "address1": "string",
    "address2": "string",
    "postalcode": "string",
    "city": "string",
    "state": "string",
    "country": "string",
    "email": "string",
    "phone": "string",
    "jurisdiction": "string"
  },
  "operatorServiceDescriptionVersion": "string",
  "supportedProfiles": [
    "Consenting"
  ],
  "operatorUrls": {
    "domain": "string",
    "linkingUri": "http://localhost/cape-dashboard/serviceLinking",
    "linkingRedirectUri": "http://localhost/cape-dashboard/pages/services/linkedServices"
  },
  "createdOnDate": "2020-04-10T10:06:55.333Z",
  "createdByUserId": "string"
}
```
