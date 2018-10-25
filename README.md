# Simple Active Directory Web Proxy

This is a simple proxy that can be spun infront of any service that needs authentication based on Active Directory.

## How to Build
This repository supports two ways to package the application, the first is a standard JAR file and the second is a Docker image. 
The Docker image is a simple wrapper around the JAR file to make it easier to deploy and manage in container environments.

### Building a JAR Package
```
./gradlew clean build -Ppatch_version=${CURRENT_PATCH_VERSION}
```
The resulting artifact will be located at `./build/libs/activedirectory-proxy*.jar`

### Building a Docker Image
```
./gradlew createDockerImage -Ppatch_version=${CURRENT_PATCH_VERSION}
```
This will build the JAR artifact and create an image that is tagged latest as well as the version number of the artifact 
(e.g. jahnelgroup/activedirectory-proxy:1.0.2). This is the preferred method of building

```
docker build -t jahnelgroup/activedirectory-proxy:latest .
```
This will create an image that is tagged latest. Note that the JAR artifact will need to be built before this, see 
[Building a JAR Package](#building-a-jar-package)

## How to Run
The application expects five (5) properties to be set during startup. They are:
* zuul.routes.service.path
* zuul.routes.service.url
* ldap.domain
* ldap.url
* ldap.searchFilter


### Running the JAR Package
If you elected to package as a JAR only, then use the following example to run the application:
```
java -jar \
    -Dzuul.routes.service.path="/**" \
    -Dzuul.routes.service.url="http://myOtherService.com:8824/" \
    -Dldap.domain="ad.myCompanyADDomain.com" \
    -Dldap.url="ldap://ad.myCompanyADDomain.com:389" \
    -Dldap.searchFilter="(&(objectClass=user)(userPrincipalName={0}))" \
    ./build/libs/activedirectory-proxy-1.0.0.jar
```

### Running the Docker Image
You can run this container by itself with `docker run`:

```
docker run -p 8080:8080 -t jahnelgroup/activedirectory-proxy:1.0.0 \
    -e "ZUUL_ROUTES_SERVICE_PATH=/**" \
    -e "ZUUL_ROUTES_SERVICE_URL=http://myOtherService.com:8824/" \
    -e "LDAP_DOMAIN=ad.myCompanyADDomain.com" \
    -e "LDAP_URL=ldap://ad.myCompanyADDomain.com:389" \
    -e "LDAP_SEARCHFILTER=(&(objectClass=user)(userPrincipalName={0}))"
```

Or in a stack along other containers with `docker-compose`:
```
...
services:
    activedirectory-proxy:
        image: jahnelgroup/activedirectory-proxy:1.0.0
        container_name: activedirectory-proxy
        environment:
            - ZUUL_ROUTES_SERVICE_PATH=/**
            - ZUUL_ROUTES_SERVICE_URL=http://myOtherService.com:8824/
            - LDAP_DOMAIN=ad.myCompanyADDomain.com
            - LDAP_URL=ldap://ad.myCompanyADDomain.com:389
            - LDAP_SEARCHFILTER=(&(objectClass=user)(userPrincipalName={0}))
        ports:
            - 8080:8080
...
```

Of course this can be extended to Kubernetes. Amazon ECS, etc configurations.

## TODO

* Make a customized login page that sources two pieces of information from the external application.yml file 
(an image of the service to display and welcome text)

