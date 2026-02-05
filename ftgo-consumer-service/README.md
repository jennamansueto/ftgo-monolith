# FTGO Consumer Service

The Consumer Service is an independent microservice extracted from the FTGO monolith. It manages customer accounts and validates orders against consumer-specific rules.

## Running the Service

The Consumer Service runs on port 8082 by default.

### Standalone Mode
```bash
./gradlew :ftgo-consumer-service:bootRun
```

### Docker
```bash
docker-compose up ftgo-consumer-service
```

## API Endpoints

### Create Consumer
```
POST /consumers
Content-Type: application/json

{
  "name": {
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

### Get Consumer
```
GET /consumers/{consumerId}
```

### Validate Order for Consumer
```
POST /consumers/{consumerId}/validate
Content-Type: application/json

{
  "orderTotal": 25.99
}
```

## Database Configuration

The Consumer Service uses a separate database (`ftgo_consumer`) from the main FTGO application. This ensures data isolation and allows independent scaling.

Database connection is configured via environment variables:
- `SPRING_DATASOURCE_URL`: JDBC URL (default: `jdbc:mysql://localhost/ftgo_consumer`)
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

## Transaction Boundary Changes

When the Consumer Service was extracted from the monolith, the following transaction boundary changes occurred:

1. **Separate Transaction Context**: Consumer validation now happens in a separate transaction within the Consumer Service, not within the Order Service's transaction.

2. **Loss of ACID Guarantees**: The `@Transactional` annotation in `OrderService.createOrder()` no longer covers consumer validation since it's now a remote HTTP call. This means:
   - If consumer validation succeeds but order creation fails, the consumer validation cannot be rolled back
   - If order creation succeeds but a subsequent operation fails, the consumer validation state remains unchanged

3. **Eventual Consistency**: The system now operates under eventual consistency rather than strong consistency for operations spanning both services.

4. **Compensating Transactions**: For critical operations, consider implementing compensating transactions (saga pattern) to handle failures across service boundaries.

## Observability

### Health Check
```
GET /actuator/health
```

### Metrics
```
GET /actuator/metrics
GET /actuator/prometheus
```

### Distributed Tracing
The service is configured to propagate trace IDs for Zipkin distributed tracing. Configure the Zipkin URL via:
- `SPRING_ZIPKIN_BASE_URL`: Zipkin server URL (default: `http://localhost:9411/`)
