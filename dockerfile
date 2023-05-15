FROM eclipse-temurin:17-jdk-alpine
MAINTAINER sanzid.nagad
VOLUME /tmp
#ARG JAR_FILE
COPY target/BillerCodeAPI-0.0.1-SNAPSHOT.jar BillerCodeAPI-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/BillerCodeAPI-0.0.1-SNAPSHOT.jar"]