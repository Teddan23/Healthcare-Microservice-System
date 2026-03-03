# Använd en officiell JDK 17 image som bas
FROM openjdk:17-jdk-slim

WORKDIR /app

# Kopiera den byggda .jar-filen från target-mappen till containern
COPY target/Fullstack-UserService.jar /app/app.jar

# Exponera porten som Spring Boot-applikationen kör på

# Starta applikationen när containern körs
ENTRYPOINT ["java", "-jar", "app.jar"]
