# CS203 Collaborative Software Development Group Project: Ryverbank API

**Members**:

* Carmen Yip Ji Yan
* Chong Jie Mi
* Emmanuel Oh Eu-Gene
* Kwek Sin Yee
* Poo Jing Fei Nikki

## Description

## Usage

### Running the REST API servlet locally

Navigate to the root of the directory (`ryverbank-api/`) and execute `mvnw spring-boot:run`

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

## Design Considerations