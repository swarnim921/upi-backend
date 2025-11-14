# upi-backend
Backend service for UPI Dashboard (Spring Boot + Java 17 + Maven).

This repository runs a Spring Boot backend that uses MongoDB as its datastore. The project can connect to a Mongo Atlas cluster (configured in `application.properties`) or a local MongoDB for development (profile `local`).

## Local MongoDB development (quick setup)

If Atlas connectivity is an issue or you want a reproducible local dev DB, start MongoDB via Docker Compose:

```powershell
docker compose up -d
```

Then run the application with the `local` profile so it connects to the local MongoDB:

```powershell
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE='local'; .\mvnw -DskipTests spring-boot:run
```

The repo includes `docker-compose.yml` and `src/main/resources/application-local.properties` which points to `mongodb://localhost:27017/upi_backend_db`.

If you still see connection errors, check application logs. The application performs a MongoDB ping on startup and will log detailed errors; you can also enable full Spring Boot debug with `--debug`.

