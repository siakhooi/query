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
docker compose up -d
```

This will start:
- MariaDB with sample fruit data
- PostgreSQL with sample animal data
- MongoDB with sample book data
- Cassandra with sample metrics data
- Query microservice on port 8080

### Step 3: Test the Service

```bash
# Check health
curl http://localhost:8080/actuator/health

# Query fruits
curl http://localhost:8080/query/fruits | jq
curl http://localhost:8080/query/fruits-color | jq

# Query animals
curl http://localhost:8080/query/animals | jq

# Query books
curl http://localhost:8080/query/books | jq
curl http://localhost:8080/query/books-fiction | jq
curl http://localhost:8080/query/books-selected-fields | jq
curl http://localhost:8080/query/books-sorted-by-title | jq
curl http://localhost:8080/query/books-sorted-multi | jq
curl http://localhost:8080/query/books-fiction-sorted | jq
curl http://localhost:8080/query/books-aggregation | jq

# Query Cassandra metrics
curl http://localhost:8080/query/metrics | jq

# Get combined results
curl http://localhost:8080/query/all | jq

curl http://localhost:8080/query/statistics | jq

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

Once the service is running, Refer Step 3 in Docker Compose for the available endpoints

## MongoDB Aggregation Pipelines

The Query microservice supports MongoDB aggregation pipelines, which can be combined with filter, fields, and sort parameters for powerful data processing.

### Configuration Example

```yaml
query:
  querysets:
    - name: books
      queries:
        # Simple aggregation pipeline
        - name: books-by-genre
          mongoQuery:
            collection: books
            pipeline: |
              [
                {"$group": {"_id": "$genre", "count": {"$sum": 1}}},
                {"$sort": {"count": -1}}
              ]
          connection: mongodb1

        # Combined: filter + pipeline + fields + sort
        - name: popular-genres
          mongoQuery:
            collection: books
            filter: '{"published": {"$gte": 2000}}'
            pipeline: |
              [
                {"$group": {"_id": "$genre", "count": {"$sum": 1}, "avgPrice": {"$avg": "$price"}}},
                {"$match": {"count": {"$gt": 5}}}
              ]
            fields: '{"_id": 1, "count": 1, "avgPrice": 1}'
            sort: '{"count": -1}'
          connection: mongodb1
```

### Pipeline Execution Order

When combining parameters, stages are executed in this order:
1. **$match** (from `filter`) - Filters documents early
2. **Custom pipeline stages** (from `pipeline`) - Your aggregation logic
3. **$project** (from `fields`) - Selects output fields
4. **$sort** (from `sort`) - Orders final results

This allows for maximum flexibility without needing to deduplicate stages.

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
