# Performance Analysis

## 1. Objective

I used Apache JMeter to check how the product-list API responded when the number of virtual users and total requests increased.

The test was run against the local Spring Boot application. It was intended as a first performance check for the project, rather than a production benchmark.

## 2. Test setup

- Tool: Apache JMeter 5.6.3
- Endpoint: `GET /api/products`
- Protocol: HTTP
- Host: `localhost`
- Port: `8080`
- Method: GET
- Loops per virtual user: 10
- Listener used: Summary Report

The total number of requests is the number of virtual users multiplied by the loop count.

## 3. Results

| Virtual users | Ramp-up (sec) | Loops | Total requests | Average (ms) | Min (ms) | Max (ms) | Std. deviation (ms) | Error % | Throughput (req/sec) |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 50 | 10 | 10 | 500 | 13 | 8 | 106 | 6.33 | 0.0 | 50.39 |
| 100 | 10 | 10 | 1,000 | 119 | 10 | 642 | 115.49 | 0.0 | 99.30 |
| 200 | 20 | 10 | 2,000 | 41 | 4 | 492 | 50.81 | 0.0 | 99.00 |
| 500 | 50 | 10 | 5,000 | 22 | 4 | 641 | 26.06 | 0.0 | 99.84 |

The figures are transcribed from the JMeter Summary Report results captured during testing.

## 4. What I observed

- Each of the four recorded runs showed a 0% error rate.
- The 500-user run completed 5,000 requests without a reported request error.
- Throughput was around 99 requests per second in the 100-, 200- and 500-user runs.
- Average response time did not increase steadily as the user count increased. The 100-user run had the highest average response time at 119 ms.
- The highest maximum response time was 642 ms in the 100-user run; the 500-user run recorded 641 ms.
- The available results do not explain why response times varied between runs.

## 5. Conclusion

In this local test setup, the `GET /api/products` endpoint completed up to 5,000 requests with 500 JMeter virtual users and a reported error rate of 0%.

The results give me an initial baseline for the endpoint. They do not prove that the application can handle the same workload in a production environment.

## 6. Limitations and possible next steps

- The tests were run locally, so they do not represent a real production network or deployment.
- Only the GET product-list endpoint was tested with JMeter.
- The runs were not repeated as a controlled benchmark with a fixed warm-up and measurement process.
- CPU and GPU readings were mentioned during testing, but a consistent resource-monitoring record was not saved.
- Memory usage, JVM garbage collection, server thread activity and MySQL connection-pool behavior were not comprehensively monitored.
- The JMeter `.jmx` file contains the test configuration, not the historical Summary Report results.

For a more complete performance investigation, I would repeat the tests under controlled conditions and record application, JVM, database and load-generator resource usage.
