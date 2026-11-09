# Smart API Performance & Reliability Platform

A REST API built with Java 17, Spring Boot and MySQL for managing product information. Along with the CRUD operations, I added request-time logging, centralized exception handling, automated tests and a JMeter load test.

The main goal of this project was to build a backend API and learn how to check its behavior when requests are sent repeatedly and under a higher load.

## Features

- Product CRUD operations: create, view, update and delete products.
- MySQL database integration using Spring Data JPA and Hibernate.
- Request logging that records the HTTP method, URI, response status and time taken.
- Centralized exception handling for missing products, invalid input and unexpected errors.
- Automated API tests using JUnit and MockMvc.
- Load testing of the product-list endpoint using Apache JMeter.

## Technologies

| Technology | Usage |
|---|---|
| Java 17 | Application language |
| Spring Boot | Backend application framework |
| Spring Web / Spring MVC | REST endpoints |
| Spring Data JPA | Database access |
| Hibernate | ORM implementation |
| MySQL | Relational database |
| Maven | Build and dependency management |
| JUnit / Spring Boot Test / MockMvc | Automated testing |
| Apache JMeter 5.6.3 | Load testing |

## Application structure

The application follows a layered structure:

```text
Client
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
MySQL
```

- **Controller:** receives HTTP requests and returns API responses.
- **Service:** handles the product operations and application logic.
- **Repository:** communicates with the database through Spring Data JPA.
- **Entity:** represents the product data stored in MySQL.
- **Performance filter:** records request details and elapsed time.
- **Global exception handler:** converts handled application errors into HTTP responses.

The performance filter runs as part of request processing. It records the request method, URI, final status code and elapsed time. For example:

```text
API Performance | Method: GET | URI: /api/products | Status: 200 | Duration: 240.24 ms
```

The duration is the time measured across the request processing covered by the filter; it should not be treated as database-query time alone.

## Product API

Base URL for local development:

```text
http://localhost:8080
```

| HTTP method | Endpoint | Description | Successful response |
|---|---|---|---:|
| POST | `/api/products` | Create a product | 201 |
| GET | `/api/products` | Get all products | 200 |
| GET | `/api/products/{id}` | Get a product by ID | 200 |
| PUT | `/api/products/{id}` | Update a product | 200 |
| DELETE | `/api/products/{id}` | Delete a product | 204 |

Product data includes `id`, `name`, `description`, `price` and `stock`.

### Example request

```http
POST /api/products
Content-Type: application/json
```

```json
{
  "name": "Wireless Mouse",
  "description": "USB wireless mouse",
  "price": 599.00,
  "stock": 25
}
```

Use the actual validation rules in the application when preparing request data. The database-generated ID is not included in this example request.

## Error handling

A global exception handler is used to keep error handling in one place.

Examples of handled outcomes:

| Situation | HTTP status |
|---|---:|
| Product does not exist | 404 Not Found |
| Request validation fails | 400 Bad Request |
| Unexpected handled server error | 500 Internal Server Error |

For example, requesting a product that does not exist returns a 404 response with a message similar to:

```text
Product not found with id: 999
```

## Automated testing

The project includes automated tests for the main product API behavior, including successful operations, invalid input and a missing product. The recorded test run completed with **6 tests passing**.

Run the tests from the project root:

```powershell
.\mvnw.cmd clean test
```

See [API testing](docs/api-testing.md) for more detail.

## Performance testing

Apache JMeter 5.6.3 was used to send repeated requests to:

```text
GET http://localhost:8080/api/products
```

The largest recorded run used 500 virtual users and 10 loops per user, producing 5,000 requests. All four recorded runs reported a 0% error rate.

| Virtual users | Requests | Average response time | Maximum response time | Error rate |
|---:|---:|---:|---:|---:|
| 50 | 500 | 13 ms | 106 ms | 0% |
| 100 | 1,000 | 119 ms | 642 ms | 0% |
| 200 | 2,000 | 41 ms | 492 ms | 0% |
| 500 | 5,000 | 22 ms | 641 ms | 0% |

These are results from local testing, not a production capacity claim. The response times varied between runs, and further controlled testing would be needed to investigate the variation and identify any bottleneck.

Full configuration, throughput figures, observations and limitations are in [Performance analysis](docs/performance-analysis.md). The JMeter test configuration is saved as `smart-api-load-test.jmx` in the project root.

## Run locally

### Requirements

- Java 17
- MySQL Server
- Git

### 1. Clone the repository

```bash
git clone <repository-url>
cd smart-api
```

### 2. Create the database

```sql
CREATE DATABASE smart_api_db;
```

### 3. Configure the database

Update `src/main/resources/application.properties` with your local MySQL URL, username and password.

Do not commit real database passwords. Use environment variables or a local configuration file excluded from Git.

### 4. Run the application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`, assuming the configured port is 8080.

### 5. Run the tests

On Windows:

```powershell
.\mvnw.cmd clean test
```

On macOS/Linux:

```bash
./mvnw clean test
```

## Repository contents

```text
smart-api/
├── src/
│   ├── main/
│   └── test/
├── docs/
│   ├── api-testing.md
│   └── performance-analysis.md
├── smart-api-load-test.jmx
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md
```

The Java package layout is kept under `src/main/java` and `src/test/java`; the exact package names are visible in the source code.

## Notes and current limitations

- The JMeter test covered `GET /api/products`; it did not load-test every CRUD endpoint.
- Tests were run locally against the development application.
- Application, JVM and database resource metrics were not comprehensively captured.
- The JMeter plan stores the test configuration. The historical Summary Report values are recorded in the performance-analysis document.
- These results are an initial project baseline and should not be used as a production scalability guarantee.
