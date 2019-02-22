# https://spring.io/guides/gs/spring-boot-docker/

FROM openjdk:8-jdk-alpine

# This is where a Spring Boot creates working directories for Tomcat by default
VOLUME /tmp

# Copy the build archive into the container
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

#  This is faster than using the indirection provided by the fat jar launcher
ARG MAIN_CLASS
ENV MAIN_CLASS=$MAIN_CLASS

ENTRYPOINT exec java -cp app:app/lib/* $MAIN_CLASS