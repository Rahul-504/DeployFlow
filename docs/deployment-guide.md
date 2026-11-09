# DeployFlow - Deployment Guide

This document explains how the Spring Boot application is packaged, how Docker Compose manages the API and MySQL containers, and how GitHub Actions automates the build and test process.

## 1. Dockerfile

The Dockerfile uses a multi-stage build to package the Spring Boot application.

### Build stage

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw clean package -DskipTests
```

The build stage uses Maven with Java 17.

- Copies the Maven configuration and wrapper.
- Downloads project dependencies.
- Copies the application source code.
- Packages the application into an executable JAR file.

Tests are skipped in this image-building step because the GitHub Actions workflow runs the automated tests separately.

### Runtime stage

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

The runtime stage uses Java 17 JRE instead of the full Maven build environment.

Only the packaged JAR is copied from the build stage. This keeps build tools out of the final runtime image.

## 2. Docker Compose

Docker Compose manages the API and MySQL services.

### API service

- Builds the application image using the Dockerfile.
- Maps host port 8080 to container port 8080.
- Receives database connection settings through environment variables.
- Waits for MySQL to become healthy before starting.

### MySQL service

- Uses the official MySQL 8.0 image.
- Creates the `smart_api_db` database.
- Uses the configured root password.
- Stores database files in the named volume `mysql_data`.

### Database health check

The MySQL service uses `mysqladmin ping` to check whether the database is ready.

The API service uses:

```yaml
depends_on:
  mysql:
    condition: service_healthy
```

This prevents the API from starting before MySQL passes its health check.

### Docker networking

Docker Compose creates an internal network for the services.

The API connects to MySQL using:

```text
jdbc:mysql://mysql:3306/smart_api_db
```

Here, `mysql` is the Compose service name. Docker's internal DNS resolves it to the MySQL container.

The API does not use `localhost` to connect to MySQL because `localhost` inside a container refers to that same container.

MySQL does not need a host port mapping for the API to communicate with it over the internal Docker network.

### Database persistence

The Compose configuration uses:

```yaml
volumes:
  mysql_data:
```

The volume stores MySQL data outside the container's writable layer.

This allows database data to remain available when containers are recreated.

## 3. Environment Variables

The project uses environment variables instead of committing database passwords to source control.

The local `.env` file supplies:

```env
DB_PASSWORD=your_local_password
```

Docker Compose passes the password to both the MySQL and API services.

The application receives:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

The `.env` file is excluded through `.gitignore` and `.dockerignore`.

GitHub Actions uses separate test database credentials for its temporary MySQL service.

## 4. GitHub Actions CI Workflow

Workflow location:

```text
.github/workflows/ci.yml
```

The workflow is triggered by:

- Push events.
- Pull request events.

### Workflow execution

1. GitHub Actions starts an Ubuntu runner.
2. The repository source code is checked out.
3. Java 17 is configured using `actions/setup-java`.
4. Maven Wrapper executable permission is enabled.
5. A MySQL 8.0 service is started for automated testing.
6. Maven executes `clean test`.
7. Docker builds the application image if the tests succeed.

### MySQL connection in CI

The workflow uses:

```text
jdbc:mysql://127.0.0.1:3306/smart_api_db
```

The Maven tests run directly on the GitHub-hosted runner, so the MySQL service is accessed through its mapped port.

This differs from Docker Compose, where the API runs inside a container and connects through the service name `mysql`.

### Test