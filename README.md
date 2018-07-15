# Building and Running the project

## Requirements
1. Java 8
2. Maven
3. Docker CE

## Building and Running:
1. Build both projects city-service and route-service (mvn clean package)
2. From the root directory execute the command "sudo docker build --file=Dockerfile.base --tag=alpine-java:base --rm=true . "
3. From the root directory execute the command "sudo docker-compose up --build"

The city-service will be exposed on port 8080 and the route-service will be exposed on port 8081

## Architecture
This project tries to follow [the clean architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html) guidelines whenever it is possible, therefore its layers are splitted in three tiers:
1. Models
2. Use Cases
3. Gateways
