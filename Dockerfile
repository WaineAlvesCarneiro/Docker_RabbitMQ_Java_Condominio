FROM eclipse-temurin:17-jdk-alpine

# curl é usado pelo healthcheck do Docker
RUN apk add --no-cache curl

WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]