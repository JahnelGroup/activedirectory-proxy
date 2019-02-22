# Simple Active Directory Web Proxy

This is a simple proxy that can be spun infront of any service that needs authentication based on Active Directory.

## How to Build
This repository supports two ways to package the application, the first is a standard Spring Boot JAR file and the second is a Docker image. 

### Building a JAR Package
```
./gradlew clean build 
```
The resulting artifact will be located at `./build/libs/activedirectory-proxy*.jar`

### Building a Docker Image
Build the docker image.

```
./gradlew clean docker
```
The resulting image will be loaded in your local system with the respective version tag:

```bash
[szgaljic@challenger images]$ docker images
REPOSITORY                                          TAG                 IMAGE ID            CREATED             SIZE
com.jahnelgroup/activedirectory-proxy               1.0.0               38378b7968bf        23 minutes ago      145MB
```

## How to Run
The application expects five (5) properties to be set during startup, they are:
* zuul.routes.service.path
* zuul.routes.service.url
* zuul.routes.ignored-patterns
* ldap.domain
* ldap.url
* ldap.searchFilter
* app.proxied-app-name
* app.instructions
* app.help

Additionally if you run this as a docker container you can change the splash `logo.png` by mounting into `/app/static/proxy/images`. 

### Running the JAR Package
If you elected to package as a JAR only, then use the following example to run the application:
```
java -jar \
    -Dzuul.routes.service.path="/**" \
    -Dzuul.routes.service.url="http://myOtherService.com:8824/" \
    -Dldap.domain="ad.myCompanyADDomain.com" \
    -Dldap.url="ldap://ad.myCompanyADDomain.com:389" \
    -Dldap.searchFilter="(&(objectClass=user)(userPrincipalName={0}))" \
    -Dapp.proxied-app-name="My App" \
    -Dapp.help="Contact help desk for support." \
    -Dapp.instructions="Login with your AD credentials." \
    ./build/libs/activedirectory-proxy-1.0.0.jar
```

### Running the Docker Image
You can run this container by itself with `docker run`:

```
docker run --name my_adproxy -d -p 8080:8080 \
    -e "ZUUL_ROUTES_SERVICE_URL=http://myOtherService.com:8824" \
    -e "LDAP_DOMAIN=ad.myCompanyADDomain.com" \
    -e "LDAP_URL=ldap://ad.myCompanyADDomain.com:389" \
    -e "LDAP_SEARCHFILTER=(&(objectClass=user)(userPrincipalName={0}))"
    -e "APP_PROXIED_APP_NAME=My App" \
    -e "APP_HELP=Contact the help desk for support." \
    -e "APP_INSTRUCTIONS=Login with your AD credentials." \       
    com.jahnelgroup/activedirectory-proxy:1.0.0
```

Or in a stack along other containers with `docker-compose`. In this example we're overriding the splash logo:

The new logo:

```bash
$ ls /opt/adproxy/images
logo.png
```

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
            - APP_PROXIED_APP_NAME: My App
            - APP_HELP: Contact the help desk for support.  
            - APP_INSTRUCTIONS: Login with your AD credentials.
        volumes:
            - /opt/adproxy/images:/app/static/proxy/images
        ports:
            - 8080:8080
...
```

Of course this can be extended to Kubernetes. Amazon ECS, etc configurations.