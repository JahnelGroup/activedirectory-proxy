# Simple Active Directory Web Proxy

This is a simple proxy that can be spun infront of any service that needs authentication based on Active Directory.

## How to Run

You can run this container by itself with `docker run`:

```
docker run -p 8080:8080 -t jahnelgroup/activedirectoryproxy:1.0.0 \
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
    activedirectoryproxy:
        image: jahnelgroup/activedirectoryproxy:1.0.0
        container_name: activedirectoryproxy
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

## TODO

* Make a customized login page that sources two pieces of information from the external application.yml file 
(an image of the service to display and welcome text)

