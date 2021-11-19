<p align="center">
<img width="400" height="200" src="https://www.cape-suite.eu/cape-dashboard/assets/images/app/cape-logo.png">
</p>

# CaPe - A Consent Based Personal Data Suite

[![Docker Pulls](https://img.shields.io/docker/pulls/capesuite/account-manager)](https://hub.docker.com/r/capesuite/account-manager)
[![Docker Build Automated](https://img.shields.io/docker/cloud/automated/capesuite/account-manager)](https://hub.docker.com/r/capesuite/account-manager)
[![Docker Build Automated](https://img.shields.io/docker/cloud/build/capesuite/account-manager)](https://hub.docker.com/r/capesuite/account-manager)

[![Documentation badge](https://img.shields.io/readthedocs/cape-suite)](https://cape-suite.readthedocs.io/)

CaPe is a consent-based and user-centric platform targeted at organizations acting as Data Processors, in the private or public sector. It enables them to take advantage of the value of personal data in compliance with GDPR while providing data subjects the natural need to detain both the use and the protection on their own data.

CaPe acts as an intermediary and creates a communication channel between Data Subjects and Data Controllers.

CaPe is a software developed by
[Engineering Ingegneria Informatica SpA](http://www.eng.it) and supported by [EIT Digital](https://www.eitdigital.eu/).

| :books: [Documentation](https://cape-suite.readthedocs.io/) | :whale: [Docker Hub](https://hub.docker.com/u/capesuite)|
|---|---|

## Content

-   [Install](#install)
-   [Usage](#usage)
-   [API](#api)
-   [CaPe Sandbox](#cape-sandbox)
-   [Support / Contact](#support)
-   [License](#license)

## Install

The instruction to install CaPe can be found at the corresponding section of
[Read The Docs](https://cape-suite.readthedocs.io/en/latest/install).

## Usage

The User Guide for CaPe Dashboards can be found at the corresponding section of
[Read The Docs](https://cape-suite.readthedocs.io/en/latest/dashboards/user-dashboard/).

## API

The CaPe APIs Introduction can be found at the corresponding section of
[Read The Docs](https://cape-suite.readthedocs.io/en/latest/api/index.md).
  
The SwaggerUI - OpenAPI 3 documentation can be found here:

- [https://www.cape-suite.eu/swagger-ui](https://www.cape-suite.eu/swagger-ui)   

## CaPe Sandbox

A demo instance of CaPe is available at the following link:

-   [`Cape Suite information site`](https://www.cape-suite.eu)
-   [`User Self-Service Dashboard instance`](https://www.cape-suite.eu/cape-dashboard)
-   [`Data Controller Dashboard instance`](https://www.cape-suite.eu/cape-service-editor)


Service integration examples:

- [CaPe Playground](https://www.cape-suite.eu/cape-playground)
- [CaPe Online Services](https://www.cape-suite.eu/cape-online-services)

<a name="support"></a>

## Support / Contact

Any feedback on this documentation is highly welcome, including bugs, typos and suggestions. You can use GitHub [issues](https://github.com/OPSILab/Cape/issues)
to provide feedback.

##### Contacts

-   Roberto Di Bernando: [_roberto.dibernardo@eng.it_](mailto:robertodibernardo@eng.it)
-   Vincenzo Savarino: [_vincenzo.savarino@eng.it_](mailto:vincenzo.savarino@eng.it)

---

## License

CaPe is licensed under [AGPLv3 License](./LICENSE).

---
## Libraries

CaPe Suite uses following libraries and frameworks.

| Name                                                                                    | Version       | License                           |
|-----------------------------------------------------------------------------------------|---------------|-----------------------------------|
| [Spring Boot](https://spring.io/projects/spring-boot)                                   | 2.4.5         | Apache License 2.0                |
| [Springdoc Openapi](https://springdoc.org)                                              | 1.5.8         | Apache License 2.0                |
| [Spring Data](https://spring.io/projects/spring-data)                                   | 2.4.5         | Apache License 2.0                |
| [Nimbus Jose JWT](https://connect2id.com/products/nimbus-jose-jwt)                      | 8.5           | Apache License 2.0                |
| [Bouncy Castle](https://www.bouncycastle.org)                                           | 1.64          | MIT                               |
| [Lombok](Nihttps://projectlombok.org/)                                                  | 1.18.12       | MIT                               |
| [Apache Commong Lang 3](https://commons.apache.org)                                     | 3.11          | Apache License 2.0                |
| [JSONSchema2Pojo Plugin](http://jsonschema2pojo.org)                                    | 1.01          | Apache License 2.0                |
| [Angular](angular.io)                                                                   | 11.2.12       | MIT                               |
| [Nebular](https://akveo.github.io/nebular)                                              | 7.0.0         | MIT                               |
| [Json-Editor](https://github.com/json-editor/json-editor)                               | 2.5.4         | MIT                               |
| [Bootstrap](https://getbootstrap.com )                                                  | 4.6.0         | MIT                               |
| [Ngx-configure](https://github.com/catrielmuller/ngx-configure)                         | 9.0.0         | ISC License                       |
| [Ng2-smart-table](https://akveo.github.io/ng2-smart-table)                              | 1.7.2         | MIT                               |
| [Rxjs](https://rxjs.dev/guide/overview)                                                 | 6.6.7         | Apache License 2.0                |
| [Ngx-translate](http://www.ngx-translate.com/)                                          | 13.0.0        | MIT                               |
| [TypeScript](https://www.typescriptlang.org)                                            | 4.1.5         | Apache License 2.0                |
| [jQuery](jquery.com)                                                                    | 3.5.1         | MIT                               |
| [D3](https://d3js.org)                                                                  | 6.6.2         | BSD                               |
| [Material-design-icons](https://github.com/google/material-design-icons)                | 3.0.1         | Apache License 2.0                |
| [Fontawesome-free](https://fontawesome.com)                                             | 5.15.3        | CC-BY-4.0                         |
| [Fontawesome-svg-core](https://www.npmjs.com/package/@fortawesome/fontawesome-svg-core) | 1.2.35        | MIT                               |                                                                  |               |                                   |
| [Java OpenJDK](https://openjdk.java.net/)                                                                      | >= 15                 | GNU General Public License Version 2.0  |
| [Apache Tomcat](https://tomcat.apache.org)                                                                     | >=9.0                | Apache License v.2.0                    |
| [MongoDB Community Server](www.mongodb.com)                                                                    | >=4.2.9              | Server Side Public License (SSPL)       |
| [Maven](https://maven.apache.org)                                                                              | >=3.5.0              | Apache License 2.0                      |
---
### External Components

CaPe uses the [Keycloak IdM](https://www.keycloak.org/) (Apache License 2.0).

---


© 2021 Engineering Ingegneria Informatica S.p.A.
