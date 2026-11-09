# API Testing

This document summarizes the functional testing and error-handling checks performed for the product API.

## 1. Automated tests

The project uses the Spring Boot testing stack, JUnit and MockMvc to test API behavior.

The recorded test run completed with:

```text
Tests run: 6
Failures: 0
Errors: 0
BUILD SUCCESS
```

Run the tests from the project root:

```powershell
.\mvnw.cmd clean test
```

The command above is for Windows PowerShell. On macOS or Linux, use:

```bash
./mvnw clean test
```

## 2. API scenarios covered

The following behaviors were implemented and exercised during testing:

| Scenario | Expected HTTP status |
|---|---:|
| Create a valid product | 201 Created |
| Get all products | 200 OK |
| Get an existing product | 200 OK |
| Update an existing product | 200 OK |
| Delete a product | 204 No Content |
| Request a product that does not exist | 404 Not Found |
| Submit invalid product data | 400 Bad Request |

The six-test count is the recorded suite result. The table lists the behaviors observed across project testing; it is not a claim that each row corresponds to a separate test method.

## 3. Validation and exception handling

Product request validation is used to reject invalid input. For example, a request with an empty product name returns a `400 Bad Request`.

A centralized exception handler (`@RestControllerAdvice`) handles application exceptions. The implemented cases include:

- A product that cannot be found returns `404 Not Found`.
- Invalid request data returns `400 Bad Request`.
- Other handled unexpected exceptions return a generic `500 Internal Server Error`.

## 4. Performance logging

The application also logs details for incoming API requests, including:

- HTTP method
- Request URI
- HTTP response status
- Request duration in milliseconds

An example log line observed during testing:

```text
API Performance | Method: GET | URI: /api/products | Status: 200 | Duration: 240.24 ms
```

This log helps inspect request duration while testing the API. It is not a replacement for a dedicated metrics or monitoring system.

## 5. Functional tests vs load tests

Functional tests check whether an endpoint returns the expected behavior and status code.

JMeter load testing sends repeated requests under a configured virtual-user workload and records response time, throughput and error rate.

The JMeter results are documented separately in [performance-analysis.md](performance-analysis.md).
