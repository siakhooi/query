# Quick Start Guide

Get the Query microservice up and running in 5 minutes!

## Using Docker Compose (Recommended)

This is the fastest way to get started with everything pre-configured.

### Step 1: Clone the Repository

```bash
git clone https://github.com/siakhooi/query.git
cd query/user-guide
```

### Step 2: Start All Services

```bash
docker-compose up -d
```

This will start:
- MariaDB with sample fruit data
- PostgreSQL with sample animal data
- Query microservice on port 8080

### Step 3: Test the Service

```bash
# Check health
curl http://localhost:8080/actuator/health

# Query fruits
curl http://localhost:8080/query/fruits | jq

# Query animals
curl http://localhost:8080/query/animals | jq

# Get combined results
curl http://localhost:8080/query/all | jq
```

### Step 4: Explore the API

Open your browser to:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

### Step 5: Stop Services

```bash
docker-compose down
```

## Using Docker Only

If you prefer to use Docker without Docker Compose:

```bash
# Pull the image
docker pull siakhooi/query:latest

# Run the service
docker run --rm -p 8080:8080 siakhooi/query:latest

# Test
curl http://localhost:8080/actuator/health
```

## Using Pre-built JAR

If you have Java 17+ installed:

```bash
# Download the latest JAR from GitHub releases
# Or build from source:
mvn clean verify

# Run
java -jar target/query-0.13.0.jar

# Test
curl http://localhost:8080/actuator/health
```

## Using Helm (Kubernetes)

```bash
# Add the Helm repository
helm repo add siakhooi https://siakhooi.github.io/helm-charts
helm repo update

# Install
helm install my-query siakhooi/query

# Port forward to access
kubectl port-forward service/my-query 8080:80

# Test
curl http://localhost:8080/actuator/health
```

## Sample Endpoints

Once the service is running, try these endpoints:

```bash
# Get all fruits (from MariaDB)
curl http://localhost:8080/query/fruits | jq

# Get all animals (from PostgreSQL)
curl http://localhost:8080/query/animals | jq

# Get combined data from both databases
curl http://localhost:8080/query/all | jq

# Get filtered fruits by color
curl http://localhost:8080/query/fruits-color | jq

# Get statistics
curl http://localhost:8080/query/statistics | jq

# View configuration
curl http://localhost:8080/config/datasource | jq
curl http://localhost:8080/config/query | jq
```

## Next Steps

- Read the complete [Usage Guide](USAGE.md) for detailed instructions
- Customize queries in `../config/query.yaml`
- Add your own database connections in `../config/datasource.yaml`
- Check out the [GitHub repository](https://github.com/siakhooi/query) for more examples

## Troubleshooting

**Port 8080 already in use?**
```bash
# Use a different port
docker run --rm -p 8081:8080 siakhooi/query:latest
```

**Can't connect to databases?**
```bash
# Check if databases are running
docker-compose ps

# View logs
docker-compose logs -f query
```

**Need help?**
- Check the [Usage Guide](USAGE.md) in this directory
- Visit [GitHub Issues](https://github.com/siakhooi/query/issues)
