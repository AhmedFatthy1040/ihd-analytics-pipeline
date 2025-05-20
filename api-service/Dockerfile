FROM gradle:8.3-jdk17 AS build
WORKDIR /app
COPY . /app
# Make sure gradlew is executable
RUN chmod +x gradlew
# Create wrapper if it doesn't exist and build
RUN ./gradlew wrapper --no-daemon
RUN ./gradlew build --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Create directory for uploads
RUN mkdir -p /app/uploads

# Set proper permissions
RUN chmod -R 755 /app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
