FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/prik2go-backend-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=default
ENTRYPOINT ["java", "-jar", "app.jar"]
