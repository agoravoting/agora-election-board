#<a name="top"></a>Agora Election Board - A Public Bulletin Board


[![License badge](https://img.shields.io/badge/license-AGPL-blue.svg)](https://opensource.org/licenses/AGPL-3.0)

* [Introduction](#introduction)
* [Design](#design)
* [API documentation](#api-documentation)
* [Build](#build)
* [License](#license)
* [Support](#support)

# Introduction

This is the code repository for Agora Election Board, the Public Bulletin Board for the [Agora Voting](https://agoravoting.com/) project. Check also the other Agora Voting [repositories](https://github.com/agoravoting).


Any feedback on this documentation is highly welcome, including bugs, typos
or things you think should be included but aren't. You can use [github issues](https://github.com/agoravoting/agora-election-board/issues/new) to provide feedback.

[Top](#top)

# Design

![Error loading image](public/architecture.png)

Agora Board has three different layers. The intermediate layer is a Generic Public Bulletin Board, which works as a purely functional typed immutable log. For that, it uses [FIWARE Orion Context Broker](https://github.com/telefonicaid/fiware-orion) to store and retrieve data, which is the lowest layer. The highest layer, the Elections Machine manages elections using the Bulletin Board. On the Elections Machine, the elections are modeled as a sequential and deterministic state machine. The state is built by reading, interpreting, and updating the Bulletin Board.

# API documentation

The lowest API is the FIWARE Orion Context Broker and you can find their API documentation on [their repository](https://github.com/telefonicaid/fiware-orion/).

The Board and Elections Machine APIs are documented using Swagger and can be viewed building the project and going to [http://localhost:9000/docs/swagger-ui/index.html?url=/docs/swagger.json#!/routes/get_docs_swagger_json](http://localhost:9000/docs/swagger-ui/index.html?url=/docs/swagger.json#!/routes/get_docs_swagger_json)

[Top](#top)

# Build

The reference distro for this project is Ubuntu 14.04 LTS. You must have scala and Lightbend Activator installed. In the future Ansible will be used for deployment.

Run the command from the project folder to get agora-election-board up and running:

    activator run

You can access the API help visiting [http://localhost:9000/docs/swagger-ui/index.html?url=/docs/swagger.json#!/routes/get_docs_swagger_json](http://localhost:9000/docs/swagger-ui/index.html?url=/docs/swagger.json#!/routes/get_docs_swagger_json) from a web browser.


[Top](#top)

# License

Agora Election Board is licensed under Affero General Public License (GPL) version 3.

[Top](#top)

# Support

[Top](#top)
