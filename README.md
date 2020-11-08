# CS203 Collaborative Software Development Group Project: Ryverbank API

**Members**:

* Carmen Yip Ji Yan
* Chong Jie Mi
* Emmanuel Oh Eu-Gene
* Kwek Sin Yee
* Poo Jing Fei Nikki

## Description

Ryverbank API is an online banking application that allows users to manage their accounts, view
curated content on banking and finance, and trade stocks in real time. Users are able to send and 
receive data via HTTP requests in JSON format. The application can be run locally or packaged into
a docker image for cloud deployment.

### Running the REST API servlet locally

Navigate to the root of the directory (`ryverbank-api/`) and execute `mvnw spring-boot:run`. 
Alternatively, on Mac/Linux, the command is `./mvnw spring-boot:run`

If the program compiles with no errors, the servlet should be running at [localhost:8080] 

## Building the docker image

For macOs and Linux:
`./mvnw spring-boot:build-image`

For Windows:
`mvnw package`

`docker build -t <image_name> .`

## Running the docker image

`docker run -it -p 8080:8080 <image name>`

## Requirements

This project is written in Java version 13.0.1, which must be installed in order for the project to
run locally. All other application dependencies and plugins are specified in the `pom.xml`
configuration file

## Documentation

This project comes with a plugin to generate javadocs for all source files. Simply run the following
command:

`mvnw javadoc:javadoc`

The generated java docs should be accessible at the `target\site\apidocs` directory