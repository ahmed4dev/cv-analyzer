FROM openjdk:17-jdk-slim

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

RUN mkdir -p /uploads
VOLUME /uploads

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]