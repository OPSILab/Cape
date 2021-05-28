# CaPe APIs - Introduction 

Cape Suite with its main components, namely Cape Server and Cape Service SDK, will expose a set of APIs implementing the CaPe functionalities.

In particular Cape Server will expose the APIs to manage.

Cape Service SDK will expose the set of APIs to be used both by the Data Controller Dashboard and directly by Data Controller/Service Provider services that will be integrated in the Cape workflow (eventually by using the provided Cape SDK angular library).

## OpenAPI Documentation

The following links provides the Swagger UI interface exposing the OpenAPI 3 documentation of CAPE APIs:

  - Complete Swagger Ui for Cape Server and Cape SDK [here](https://www.cape-suite.eu/swagger-ui)
  - Simplified Swagger Ui only documenting Cape SDK APIs, to easily identify APIs that either will be used by Cape Sdk Angular library or from sctrach directly from Service Frontend. [here](https://www.cape-suite.eu/cape-service-sdk/swagger-ui.html)
  
APis can be directly tried:

 -  By clicking at first on `Authorize` either:
     * Using client (e.g. `cape-server` and `cape-service-sdk`) and credentials of an user registered in the CaPe Idm
     * Copy as Bearer directly a JWT token issued by Cape Idm)
	 
 -  Then click each `Try it out` button.
  

The next section will provide a Walktrough in the API set provided by the Cape considering the workflow described in [the Cape Workflow section](../workflow/workflow).

---

Any feedback on this documentation is highly welcome, including bug reports and
suggestions. Please send the feedback through
[GitHub](https://github.com/OPSILab/Cape). Thanks!

