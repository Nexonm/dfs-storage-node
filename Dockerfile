# Stage 1: Build the application
FROM gradle:8-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Stage 2: Run the built application in a minimal runtime environment
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/storage-node-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]