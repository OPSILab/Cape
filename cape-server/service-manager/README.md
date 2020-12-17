#-------------------------------------------------------------------------------
# CaPe - a Consent Based Personal Data Suite
#  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
# 
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#-------------------------------------------------------------------------------
---

# CaPe Service Manager
This is a part of CaPe implementation.

## Description
The Service Manager component is responsible for the registration of the services and the mapping of their data structure with Personal Data taxonomy. Each data field is mapped with a personal data field (pdata) belonging to a set of Personal Data Category. It is possible to modify the structure of Personal Taxonomy by adding  or removing category or related data fields The module provides a set of REST API for the registration of a services and the retrieve of its mapping. 


## Prerequisites
-   [Git](https://git-scm.com/downloads)
-   [Maven](https://maven.apache.org/download.cgi)
- 	Java 1.8.*+
- 	Tomcat 8.*+
- 	MongoDB 3.3.*+

## Documentation
- [Documentation ](doc/)
- [Deployment](doc/deployment.md)
- [API documentation](doc/api/)

## Support 
[CaPe]()

## Copying and License
This code is licensed under [MIT License (MIT)](LICENSE)
