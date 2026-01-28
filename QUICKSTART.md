# Quick Start Guide

## Running the Application

1. **Build and Run:**
   ```bash
   cd anz-tokenisation-service
   mvn spring-boot:run
   ```

2. **Wait for startup** (you should see):
   ```
   Started TokenisationServiceApplication in X.XXX seconds
   ```

3. **Test the API** using curl:

   **Tokenise:**
   ```bash
   curl -X POST http://localhost:3000/tokenise \
     -H "Content-Type: application/json" \
     -d '["4111-1111-1111-1111", "4444-3333-2222-1111"]'
   ```

   **Detokenise** (use tokens from previous response):
   ```bash
   curl -X POST http://localhost:3000/detokenise \
     -H "Content-Type: application/json" \
     -d '["YOUR-TOKEN-1", "YOUR-TOKEN-2"]'
   ```

## Running Tests

```bash
mvn test
```

## Using the Test Script

```bash
./scripts/test-api.sh
```

This script will:
- Tokenise sample account numbers.
- Detokenise the tokens.
- Verify idempotency.

## Viewing the Database (H2 Console)

1. Navigate to: http://localhost:3000/h2-console
2. Use these settings:
   - JDBC URL: `jdbc:h2:mem:tokendb`
   - Username: `sa`
   - Password: (leave empty)
3. Click "Connect".
4. Run SQL queries like:
   ```sql
   SELECT * FROM TOKENS;
   ```
