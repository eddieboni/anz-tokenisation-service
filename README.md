# ANZ Tokenisation Service

A basic Spring Boot tokenisation service that swaps sensitive account numbers for randomised tokens and vice versa.

## Features

- **Tokenisation**: Convert account numbers to secure random tokens.
- **Detokenisation**: Convert tokens back to original account numbers.
- **In Memory Storage**: Uses H2 database for fast and temporary storage.
- **Token Persistence**: Same account number always returns the same token.
- **Comprehensive Tests**: Unit and integration tests included.

## Technology Stack

- Java 21.
- Maven.
- Spring Boot 3.2.1.
- Spring Data JPA.
- H2 In Memory Database.
- JUnit 5 & Mockito.

## Prerequisites

- Java 21 or higher.
- Maven 3.6 or higher.

## Quick Start

### Build the Project

```bash
mvn clean package
```

### Run the Application

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:3000`

### Run Tests

```bash
mvn test
```

## API Endpoints

### 1. Tokenise Account Numbers

Converts a list of account numbers into tokens.

**Endpoint:** `POST /tokenise`

**Request:**
```bash
curl -X POST http://localhost:3000/tokenise \
  -H "Content-Type: application/json" \
  -d '["4111-1111-1111-1111", "4444-3333-2222-1111", "4444-1111-2222-3333"]'
```

**Response:**
```json
[
  "fvMymE7X0Je1IzMDgWooV5iGBPw0yoFy",
  "L4hKuBJHxe67ENSKLVbdIH8NhFefPui2",
  "ZA5isc0kVUfvlxTE5m2dxIY8AG76KoP3"
]
```

### 2. Detokenise Tokens

Converts tokens back to their original account numbers.

**Endpoint:** `POST /detokenise`

**Request:**
```bash
curl -X POST http://localhost:3000/detokenise \
  -H "Content-Type: application/json" \
  -d '["fvMymE7X0Je1IzMDgWooV5iGBPw0yoFy", "L4hKuBJHxe67ENSKLVbdIH8NhFefPui2", "ZA5isc0kVUfvlxTE5m2dxIY8AG76KoP3"]'
```

**Response:**
```json
[
  "4111-1111-1111-1111",
  "4444-3333-2222-1111",
  "4444-1111-2222-3333"
]
```

## Design Decisions

### 1. Token Generation
- Uses `SecureRandom` for cryptographically secure random token generation.
- Tokens are Base64 URL safe encoded (32 characters).
- Uniqueness is guaranteed by checking against existing tokens before saving.

### 2. Token Persistence
- Same account number always receives the same token (idempotent).
- Tokens are stored in an H2 in memory database.
- Data is lost when the application stops (excerise).

### 3. Database Schema
- Basic `tokens` table with columns: `id`, `token`, `account_number`.
- Unique constraint on `token` column.
- Index on `account_number` for fast lookups.

### 4. Error Handling
- Returns HTTP 400 for empty or null input.
- Returns `null` in the response array for tokens that don't exist during detokenisation.

### 5. Testing Strategy
- **Unit Tests**: Service layer logic with mocked dependencies.
- **Controller Tests**: REST endpoint testing with MockMvc.
- **Integration Tests**: End to end flows with full Spring context.

## Production Considerations

To make this production ready, the following enhancements would be needed:

### Security
- **Authentication & Authorisation**: Implement OAuth2 or JWT based authentication.
- **TLS/HTTPS**: Enforce encrypted connections.
- **Rate Limiting**: Prevent abuse and DDoS attacks.
- **Input Validation**: Validate account number formats (e.g. Luhn algorithm for card numbers).
- **Audit Logging**: Track who tokenised/detokenised what and when.

### Data Management
- **Persistent Database**: Use PostgreSQL, MySQL, or encrypted cloud storage.
- **Encryption at Rest**: Encrypt sensitive data in the database.
- **Data Retention Policies**: Implement TTL for tokens.
- **Backup & Recovery**: Regular backups of token mappings.

### Performance & Scalability
- **Caching**: Add Redis/Memcached for frequently accessed tokens.
- **Connection Pooling**: Optimise database connections.
- **Horizontal Scaling**: Support multiple instances with shared database.
- **Async Processing**: For bulk tokenisation operations.

### Monitoring & Observability
- **Metrics**: Track tokenisation rates, error rates, latency (Prometheus/Micrometer).
- **Logging**: Structured logging with correlation IDs (ELK stack).
- **Health Checks**: Implement `/actuator/health` endpoints.
- **Distributed Tracing**: Add Zipkin or Jaeger.

### API Improvements
- **Versioning**: Add API versioning (e.g., `/v1/tokenise`).
- **Pagination**: For large batch operations.
- **Async APIs**: Return job IDs for large batches.
- **Error Responses**: Standardised error format with error codes.
- **OpenAPI/Swagger**: API documentation.

### Operational
- **Configuration Management**: Externalise config (Spring Cloud Config).
- **CI/CD Pipeline**: Automated testing and deployment.
- **Container Support**: Docker images and Kubernetes manifests.
- **Blue-Green Deployment**: Zero-downtime deployments.

## Project Structure

```
anz-tokenisation-service/
├── src/
│   ├── main/
│   │   ├── java/com/anz/tokenisation/
│   │   │   ├── TokenisationServiceApplication.java
│   │   │   ├── controller/
│   │   │   │   └── TokenisationController.java
│   │   │   ├── service/
│   │   │   │   └── TokenisationService.java
│   │   │   ├── entity/
│   │   │   │   └── Token.java
│   │   │   └── repository/
│   │   │       └── TokenRepository.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/anz/tokenisation/
│           ├── TokenisationServiceIntegrationTest.java
│           ├── controller/
│           │   └── TokenisationControllerTest.java
│           └── service/
│               └── TokenisationServiceTest.java
└── pom.xml
```

## H2 Console (Optional)

The H2 console is enabled for debugging purposes:

- URL: `http://localhost:3000/h2-console`
- JDBC URL: `jdbc:h2:mem:tokendb`
- Username: `sa`
- Password: (leave empty).

## License

This is a coding exercise project and is not intended for production use.
