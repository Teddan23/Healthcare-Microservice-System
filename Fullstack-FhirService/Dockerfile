# Använd en officiell JDK 17 image som bas
FROM openjdk:17-jdk-slim

WORKDIR /app

# Kopiera den byggda .jar-filen från target-mappen till containern
COPY target/Fullstack-FhirService.jar /app/app.jar

# Starta applikationen när containern körs
ENTRYPOINT ["java", "-jar", "app.jar"]
