# syntax=docker/dockerfile:1

# Multi-stage build for Spring Boot (Maven) application
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /workspace

# Copy mvn wrapper and download dependencies (cache .m2)
COPY mvnw pom.xml .
COPY .mvn .mvn
RUN chmod +x mvnw

RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -B

# Copy source and package
COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -DskipTests package -DskipDocker -B

# Extract layers for optimized image
FROM build AS extract
WORKDIR /workspace
RUN java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

# Final runtime image
FROM eclipse-temurin:17-jre-jammy

ARG UID=10001
RUN adduser --disabled-password --gecos "" --home "/nonexistent" --shell "/sbin/nologin" --no-create-home --uid "${UID}" appuser
USER appuser

WORKDIR /app
COPY --from=extract /workspace/target/extracted/dependencies/ .
COPY --from=extract /workspace/target/extracted/spring-boot-loader/ .
COPY --from=extract /workspace/target/extracted/snapshot-dependencies/ .
COPY --from=extract /workspace/target/extracted/application/ .

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

