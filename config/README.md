# Configuration Files

This directory contains sample configuration files for the Query microservice. These files are used for Docker and Docker Compose deployments.

## Files

- **application.yaml** - Main application configuration
- **datasource.yaml** - Database connection definitions
- **query.yaml** - Query definitions and querysets

## Usage

### With Docker

```bash
docker run --rm -p 8080:8080 \
  -v $(pwd)/config:/config:ro \
  -e SPRING_CONFIG_ADDITIONAL_LOCATION=file:/config/ \
  siakhooi/query:latest
```

### With Docker Compose

The `docker-compose.yml` in the parent directory automatically mounts these configuration files.

```bash
docker-compose up -d
```

## Customization

1. Copy these files to your desired location
2. Modify the database connection details in `datasource.yaml`
3. Customize queries in `query.yaml`
4. Adjust logging and management settings in `application.yaml`

## Database Connection Notes

### Docker Compose
When using Docker Compose, use the service names as hostnames:
- MariaDB: `mariadb:3306`
- PostgreSQL: `postgres:5432`

### Local Development
When running locally, use:
- MariaDB: `localhost:3306`
- PostgreSQL: `localhost:5432`

### Kubernetes
When deploying to Kubernetes with Helm, use Kubernetes service names:
- MariaDB: `mariadb-service:3306`
- PostgreSQL: `postgres-service:5432`
