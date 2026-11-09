# DeployFlow

A containerized Spring Boot REST API with MySQL and an automated CI pipeline using GitHub Actions.

DeployFlow demonstrates how to package a Java application using Docker, run the application and its database together using Docker Compose, and automate testing and Docker image creation whenever code is pushed to GitHub.

The project reuses the Smart API developed in my previous project and focuses on its containerization and CI automation.

## Features

- Containerized the Spring Boot application using a multi-stage Dockerfile.
- Configured MySQL as a separate Docker container.
- Used Docker Compose to start the API and database together with one command.
- Configured a MySQL health check to ensure the database is ready before starting the API.
- Used a named Docker volume to persist database data.
- Created a GitHub Actions CI workflow to run Maven tests automatically.
- Configured the CI pipeline to build a Docker image after successful tests.

## Technologies

| Technology | Usage |
|---|---|
| Java 17 | Application language |
| Spring Boot | Backend framework |
| MySQL 8.0 | Relational database |
| Maven | Build and dependency management |
| Docker | Application containerization |
| Docker Compose | Managing API and database containers |
| GitHub Actions | Continuous Integration |
| Git | Version control |

## Application Architecture

```text
              Developer
                  |
                  v
              Git Push
                  |
                  v
             GitHub Repo
                  |
                  v
           GitHub Actions
                  |
          +-------+-------+
          |               |
          v               v
       Maven Tests    Docker Build
          |
          v
       Test Result


        Docker Compose
              |
       +------+------+
       |             |
       v             v
   Spring Boot     MySQL
   API Container   Container
       |             |
       +-------------+
         Internal
        Docker Network
```

The API connects to MySQL using the Compose service name `mysql`, rather than `localhost`.

## Docker Configuration

### Dockerfile

The application uses a multi-stage Docker build.

- **Build stage:** Uses Maven and Java 17 to download dependencies and package the Spring Boot application into a JAR file.
- **Runtime stage:** Uses a lightweight Java 17 JRE image and copies the generated JAR file into the runtime container.

This keeps Maven and other build tools out of the final runtime image.

### Docker Compose

Docker Compose manages two services:

| Service | Purpose |
|---|---|
| `api` | Runs the Spring Boot application on port 8080 |
| `mysql` | Stores application data using MySQL 8.0 |

Additional configuration:
- MySQL health check to verify database readiness.
- API startup depends on the MySQL health check.
- Named volume `mysql_data` preserves database data across container restarts.
- Database credentials are supplied through environment variables.

## Run the Application

### Requirements

- Docker Desktop
- Git

### 1. Clone the repository

```bash
git clone https://github.com/Rahul-504/DeployFlow.git
cd DeployFlow
```

### 2. Configure environment variables

Create a `.env` file in the project root:

```env
DB_PASSWORD=your_local_password
```

Replace the example value with a strong local password.

The `.env` file is excluded from Git using `.gitignore`.

### 3. Start the application

Run:

```bash
docker compose up --build
```

Docker Compose builds the API image, starts MySQL, waits for the database health check, and starts the API.

### 4. Verify the application

Open:

```text
http://localhost:8080/api/products
```

The API should return the product list as a JSON response.

To check container status, run:

```bash
docker compose ps
```

### 5. Stop the application

```bash
docker compose down
```

The named MySQL volume is preserved when using this command. Database data is not deleted.

## Continuous Integration

The project uses GitHub Actions for Continuous Integration.

Workflow file:

```text
.github/workflows/ci.yml
```

The workflow runs on pushes and pull requests.

### CI Pipeline Steps

1. **Checkout source code:** Downloads the repository onto a GitHub-hosted Ubuntu runner.
2. **Set up Java 17:** Configures the Java environment.
3. **Start MySQL:** Runs a MySQL 8.0 service for automated tests.
4. **Run Maven tests:** Executes `./mvnw -B clean test`.
5. **Build Docker image:** Builds the application image after successful tests.

The CI environment uses its own temporary MySQL database and test credentials. These are separate from the local Docker Compose configuration.

## Project Structure

```text
DeployFlow/
├── .github/
│   └── workflows/
│       └── ci.yml
├── .mvn/
├── docs/
├── src/
│   ├── main/
│   └── test/
├── .dockerignore
├── .env
├── .gitignore
├── Dockerfile
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

The `.env` file is local configuration and is not committed to the repository.

## API Overview

DeployFlow uses the existing Smart API product endpoints.

| HTTP Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Create a product |
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get a product by ID |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |

## Related Documentation

- `docs/api-testing.md` – API testing details.
- `docs/performance-analysis.md` – Previous API performance testing results.

## Project Scope

This project focuses on containerization, multi-container application setup, database configuration and automated CI.

The GitHub Actions workflow builds the Docker image locally on its runner. It does not currently publish the image to a container registry or deploy the application to a cloud platform.