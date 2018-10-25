FROM openjdk:8-jdk-alpine

LABEL maintainer="jbeckman@jahnelgroup.com"

EXPOSE 8080

RUN mkdir -p /usr/activedirectory-proxy/logs
VOLUME /usr/activedirectory-proxy/logs

COPY build/libs/activedirectory-proxy*.jar /usr/activedirectory-proxy/activedirectory-proxy.jar

WORKDIR /usr/activedirectory-proxy
ENTRYPOINT ["java", "-jar", "./activedirectory-proxy.jar"]
