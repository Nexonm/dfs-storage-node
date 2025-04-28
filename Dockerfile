# Stage 1: Build with JDK and create custom JRE
FROM gradle:8-jdk21-alpine AS build
WORKDIR /app
COPY . .

# Build the application
RUN gradle build --no-daemon && \
    gradle bootJar

# Create custom minimal JRE with jlink
RUN jlink --strip-debug \
          --no-header-files \
          --no-man-pages \
          --compress=2 \
          --add-modules java.base,java.logging,java.sql,java.naming,java.desktop,\
java.management,java.security.jgss,java.instrument,\
jdk.unsupported,java.xml,jdk.crypto.ec \
          --output /custom-jre

# Stage 2: Minimal runtime with custom JRE
FROM alpine:3.19
WORKDIR /app

# Install necessary libraries
RUN apk add --no-cache bash tzdata && \
    mkdir -p /opt/app

# Copy custom JRE and application jar
COPY --from=build /custom-jre /opt/jre
COPY --from=build /app/build/libs/*.jar /opt/app/app.jar

# Set environment variables
ENV PATH="/opt/jre/bin:${PATH}" \
    JAVA_HOME="/opt/jre"

# Expose application port
EXPOSE 8090

# Set the entry point
ENTRYPOINT ["/opt/jre/bin/java", "-jar", "/opt/app/app.jar"]
