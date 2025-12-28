# Query Microservice - Usage Guide

This guide provides detailed instructions on how to run the Query microservice using different deployment methods.

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Running the JAR](#running-the-jar)
- [Running with Docker](#running-with-docker)
- [Running with Docker Compose](#running-with-docker-compose)
- [Running with Helm Chart](#running-with-helm-chart)
- [Configuration](#configuration)
- [Sample Data Setup](#sample-data-setup)
- [Testing the Service](#testing-the-service)

## Overview

Query is a microservice that queries backend databases (MariaDB, PostgreSQL, MongoDB, and Cassandra) and returns results as JSON. It provides:
- Configurable data connections (JDBC, MongoDB, and Cassandra)
- Multiple query definitions
- RESTful API endpoints
- Ability to combine data from multiple database types
- Health monitoring via Spring Boot Actuator

## Prerequisites

### For Running JAR
- Java 17 or higher
- Maven 3.6+ (for building from source)
- Access to MariaDB, PostgreSQL, MongoDB, and/or Cassandra databases

### For Running Docker
- Docker 20.10+

### For Running Docker Compose
- Docker 20.10+
- Docker Compose v2.0+

### For Running Helm Chart
- Kubernetes cluster (1.20+)
- Helm 3.0+
- kubectl configured to access your cluster

## Running the JAR

### 1. Build the Application

```bash
# Clone the repository
git clone https://github.com/siakhooi/query.git
cd query

# Build with Maven
mvn clean verify

# The JAR file will be created in the target directory
# Example: target/query-0.13.0.jar
```

### 2. Configure the Application

Create or modify the configuration files in `src/main/resources/` or use the sample files in `../config/`:
- `application.yaml` - Main application configuration
- `datasource.yaml` - Database connection definitions
- `query.yaml` - Query definitions

See [Configuration](#configuration) section for details.

### 3. Run the JAR

```bash
# Run with default configuration
java -jar target/query-0.13.0.jar

# Run with custom configuration files
java -jar target/query-0.13.0.jar \
  --spring.config.additional-location=file:../config/

# Run with specific profile
java -jar target/query-0.13.0.jar --spring.profiles.active=production
```

The service will start on port 8080 by default.

## Running with Docker

### 1. Pull the Docker Image

```bash
docker pull siakhooi/query:latest
```

### 2. Run with Default Configuration

```bash
docker run --rm -p 8080:8080 siakhooi/query:latest
```

### 3. Run with Custom Configuration

To pass custom configuration files to the Docker container, you need to:
1. **Mount configuration files** using `-v` (volume mount)
2. **Set environment variable** `SPRING_CONFIG_ADDITIONAL_LOCATION` to tell Spring Boot where to find them

#### Option A: Mount Individual Files

```bash
docker run --rm -p 8080:8080 \
  -v $(pwd)/../config/datasource.yaml:/config/datasource.yaml \
  -v $(pwd)/../config/query.yaml:/config/query.yaml \
  -e SPRING_CONFIG_ADDITIONAL_LOCATION=file:/config/ \
  siakhooi/query:latest
```

#### Option B: Mount Entire Config Directory

This is simpler - mount the entire config directory:

```bash
docker run --rm -p 8080:8080 \
  -v $(pwd)/../config:/config:ro \
  -e SPRING_CONFIG_ADDITIONAL_LOCATION=file:/config/ \
  siakhooi/query:latest
```

**Note:** The `:ro` flag makes the mount read-only for security.

#### Option C: Using Absolute Paths

If you prefer absolute paths:

```bash
docker run --rm -p 8080:8080 \
  -v /path/to/your/config:/config:ro \
  -e SPRING_CONFIG_ADDITIONAL_LOCATION=file:/config/ \
  siakhooi/query:latest
```

#### Option D: Override Specific Settings via Environment Variables

You can also override individual settings using environment variables:

```bash
docker run --rm -p 8080:8080 \
  -v $(pwd)/../config:/config:ro \
  -e SPRING_CONFIG_ADDITIONAL_LOCATION=file:/config/ \
  -e LOGGING_LEVEL_SING_APP_QUERY=DEBUG \
  siakhooi/query:latest
```

#### Practical Example: Complete Workflow

Here's a complete example from creating configs to running the container:

```bash
# 1. Create a config directory
mkdir -p my-query-config

# 2. Create datasource.yaml
cat > my-query-config/datasource.yaml << 'EOF'
datasource:
  connections:
    - name: "mydb"
      url: "jdbc:postgresql://host.docker.internal:5432/mydb"
      username: "myuser"
      password: "mypass"
EOF

# 3. Create query.yaml
cat > my-query-config/query.yaml << 'EOF'
query:
  querysets:
    - name: users
      queries:
        - name: all-users
          connection: mydb
          queryString: SELECT * FROM users
EOF

# 4. Run the container with your configs
docker run --rm -p 8080:8080 \
  -v $(pwd)/my-query-config:/config:ro \
  -e SPRING_CONFIG_ADDITIONAL_LOCATION=file:/config/ \
  siakhooi/query:latest

# 5. Test the endpoint
curl http://localhost:8080/query/users | jq
```

**Tip:** Use `host.docker.internal` instead of `localhost` to access services running on your host machine from inside the Docker container.

### 4. Run with Database Links

If running databases in Docker, link them:

```bash
# Start MariaDB
docker run -d --name mariadb \
  -e MYSQL_ROOT_PASSWORD=rootpass \
  -e MYSQL_DATABASE=fruitdb \
  -e MYSQL_USER=fruituser \
  -e MYSQL_PASSWORD=fruitpass \
  -p 3306:3306 \
  mariadb:latest

# Start PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_DB=animaldb \
  -e POSTGRES_USER=animaluser \
  -e POSTGRES_PASSWORD=password123 \
  -p 5432:5432 \
  postgres:16

# Start MongoDB
docker run -d --name mongodb \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=adminpass \
  -p 27017:27017 \
  mongo:7

# Run query service with network access to databases
docker run --rm -p 8080:8080 \
  --link mariadb:mariadb \
  --link postgres:postgres \
  --link mongodb:mongodb \
  -v $(pwd)/../config:/config \
  -e SPRING_CONFIG_ADDITIONAL_LOCATION=file:/config/ \
  siakhooi/query:latest
```

## Running with Docker Compose

Docker Compose provides the easiest way to run the complete stack with databases.

### 1. Use the Provided docker-compose.yml

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f query

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### 2. Initialize Sample Data

Sample data is automatically loaded during container initialization through the init scripts mounted in docker-compose.yml:
- MariaDB: `../sample/fruit.sql`
- PostgreSQL: `../sample/animal.sql`
- MongoDB: `../sample/book.js`

If you need to reload the data manually:

```bash
# Load fruit data into MariaDB
docker-compose exec mariadb mysql -uroot -prootpass < ../sample/fruit.sql

# Load animal data into PostgreSQL
docker-compose exec postgres psql -U postgres < ../sample/animal.sql

# Load book data into MongoDB
docker-compose exec mongodb mongosh --username admin --password adminpass --authenticationDatabase admin /docker-entrypoint-initdb.d/book.js
```

See the [docker-compose.yml](docker-compose.yml) file for the complete configuration.

## Running with Helm Chart

### 1. Add the Helm Repository

```bash
helm repo add siakhooi https://siakhooi.github.io/helm-charts
helm repo update
```

### 2. Install with Default Values

```bash
helm install my-query siakhooi/query
```

### 3. Install with Custom Values

Create a `custom-values.yaml` file:

```yaml
deployment:
  image:
    tag: "0.13.0"

  resources:
    limits:
      cpu: "2"
      memory: 1Gi
    requests:
      cpu: "500m"
      memory: 512Mi

  readinessProbe:
    enabled: true
  livenessProbe:
    enabled: true
  startupProbe:
    enabled: true

# Custom datasource configuration
default_datasource_yaml:
  datasource:
    connections:
      - name: "fruitdb"
        url: "jdbc:mariadb://mariadb-service:3306/fruitdb"
        username: "fruituser"
        password: "fruitpass"
        maximumPoolSize: 10
        minimumIdle: 2

# Custom query configuration
default_query_yaml:
  query:
    querysets:
      - name: fruits
        queries:
          - name: fruits
            connection: fruitdb
            queryString: SELECT name, color, taste FROM fruits
```

Install with custom values:

```bash
helm install my-query siakhooi/query -f custom-values.yaml
```

### 4. Upgrade the Release

```bash
helm upgrade my-query siakhooi/query -f custom-values.yaml
```

### 5. Uninstall

```bash
helm uninstall my-query
```

### 6. Access the Service

```bash
# Port forward to access locally
kubectl port-forward service/my-query 8080:80

# Or create an Ingress/LoadBalancer service
```

## Configuration

### Application Configuration (application.yaml)

```yaml
management:
  endpoints:
    shutdown:
      enabled: true
    web:
      exposure:
        include: "*"
  endpoint:
    env:
      show-values: always
    configprops:
      show-values: always

logging:
  level:
    "org.springframework.cloud.kubernetes": DEBUG
    "org.springframework.web": DEBUG
    "sing.app.query": DEBUG

spring:
  config:
    import:
      - datasource.yaml
      - query.yaml
```

### Database Connections (datasource.yaml)

```yaml
datasource:
  connections:
    # MariaDB example (JDBC)
    - name: "fruitdb"
      type: "jdbc"
      url: "jdbc:mariadb://localhost:3306/fruitdb"
      username: "fruituser"
      password: "fruitpass"
      maximumPoolSize: 10
      minimumIdle: 2
      connectionTimeout: 30000
      idleTimeout: 600000
      maxLifetime: 1800000

    # PostgreSQL example (JDBC)
    - name: "animaldb"
      type: "jdbc"
      url: "jdbc:postgresql://localhost:5432/animaldb"
      username: "animaluser"
      password: "password123"
      maximumPoolSize: 10
      minimumIdle: 2

    # MongoDB example
    - name: "bookdb"
      type: "mongodb"
      url: "mongodb://localhost:27017"
      database: "bookdb"
      username: "bookuser"
      password: "bookpass"

    # Cassandra example
    - name: "cassandradb"
      type: "cassandra"
      url: "cassandra://localhost:9042"
      datacenter: "datacenter1"
      keyspace: "metrics"
      username: "cassuser"
      password: "casspass"
```

### Query Definitions (query.yaml)

```yaml
query:
  querysets:
    # Queryset that combines multiple queries from different database types
    - name: all
      queries:
        - name: fruits
          connection: fruitdb
          queryString: SELECT name, color, taste FROM fruits
        - name: animals
          connection: animaldb
          queryString: SELECT name, species, age, habitat, diet FROM animals

    # Individual querysets
    - name: fruits
      queries:
        - name: fruits
          connection: fruitdb
          queryString: SELECT name, color, taste FROM fruits

    - name: animals
      queries:
        - name: animals
          connection: animaldb
          queryString: SELECT name, species, age, habitat, diet FROM animals

    # MongoDB queries
    - name: books
      queries:
        - name: books
          connection: bookdb
          mongoQuery:
            collection: books
        - name: metrics
          connection: cassandradb
          queryString: SELECT hostname, cpu_usage, recorded_at FROM metrics.cpu LIMIT 10

    # Filtered queries
    - name: fruits-color
      queries:
        - name: fruits-red
          connection: fruitdb
          queryString: SELECT name, color, taste FROM fruits WHERE color='Red'
        - name: fruits-yellow
          connection: fruitdb
          queryString: SELECT name, color, taste FROM fruits WHERE color='Yellow'

    # MongoDB filtered query
    - name: books-fiction
      queries:
        - name: books-fiction
          connection: bookdb
          mongoQuery:
            collection: books
            filter: '{"genre":"Fiction"}'

    # Cassandra metrics queries
    - name: metrics
      queries:
        - name: cpu-metrics
          connection: cassandradb
          queryString: SELECT hostname, cpu_usage, recorded_at FROM metrics.cpu LIMIT 25

    # Combined queryset from JDBC and MongoDB (and optionally Cassandra)
    - name: combined
      queries:
        - name: fruits
          connection: fruitdb
          queryString: SELECT name, color, taste FROM fruits
        - name: animals
          connection: animaldb
          queryString: SELECT name, species, age, habitat, diet FROM animals
        - name: books
          connection: bookdb
          mongoQuery:
            collection: books
```

## Sample Data Setup

### MariaDB - Fruits Database

The `sample/fruit.sql` file contains sample data for testing:

```bash
# If using Docker
docker-compose exec mariadb mysql -uroot -prootpass < sample/fruit.sql

# If using local MariaDB
mysql -h localhost -u root -p < sample/fruit.sql
```

This creates:
- Database: `fruitdb`
- User: `fruituser` with password `fruitpass`
- Table: `fruits` with 10 sample fruit records

### PostgreSQL - Animals Database

The `sample/animal.sql` file contains sample data for testing:

```bash
# If using Docker
docker-compose exec postgres psql -U postgres < sample/animal.sql

# If using local PostgreSQL
psql -h localhost -U postgres < sample/animal.sql
```

This creates:
- Database: `animaldb`
- User: `animaluser` with password `password123`
- Table: `animals` with 20 sample animal records

### MongoDB - Books Database

The `sample/book.js` file contains sample data for testing:

```bash
# If using Docker
docker-compose exec mongodb mongosh --username admin --password adminpass --authenticationDatabase admin /docker-entrypoint-initdb.d/book.js

# If using local MongoDB
mongosh --username admin --password adminpass --authenticationDatabase admin < sample/book.js
```

This creates:
- Database: `bookdb`
- User: `bookuser` with password `bookpass`
- Collection: `books` with 10 sample book records

### Cassandra - Metrics Keyspace

The `sample/cassandra.cql` file contains the schema and sample CPU metrics data.

```bash
# If using Docker Compose (automatically executed by cassandra-init, rerun manually if needed)
docker-compose exec cassandra cqlsh -f /tmp/cassandra.cql

# If using docker-compose outside user-guide, point to sample file
docker-compose exec cassandra cqlsh -f ../sample/cassandra.cql

# If using local Cassandra
cqlsh -f sample/cassandra.cql
```

This creates:
- Keyspace: `metrics`
- Table: `metrics.cpu`
- Sample rows for hosts like `app-server-1`, `worker-01`, etc.

### MongoDB Query Format

MongoDB queries use the nested `mongoQuery` object in `query.yaml`. Populate any combination of the following fields as needed:

```yaml
mongoQuery:
  collection: books
  filter: '{"genre":"Fiction"}'
  fields: '{"title": 1, "author": 1, "_id": 0}'
  sort: '{"year": -1}'
  pipeline: |
    [
      {"$group": {"_id": "$genre", "count": {"$sum": 1}}},
      {"$match": {"count": {"$gte": 2}}}
    ]
```

Examples:
- `mongoQuery.collection: books` – get all books
- `mongoQuery` with `collection` + `filter: '{"genre":"Fiction"}'` – get fiction books
- `mongoQuery` with `collection` + `filter: '{"year":{"$gt":2000}}'` – get books published after 2000

## Testing the Service

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Query Endpoints

```bash
# Get all fruits
curl http://localhost:8080/query/fruits | jq

# Get all animals
curl http://localhost:8080/query/animals | jq

# Get Cassandra metrics
curl http://localhost:8080/query/metrics | jq

# Get combined results (fruits, animals, books, metrics)
curl http://localhost:8080/query/all | jq

# Get all books (MongoDB)
curl http://localhost:8080/query/books | jq

# Get filtered books by genre (MongoDB)
curl http://localhost:8080/query/books-fiction | jq

# Get combined results from JDBC and MongoDB
curl http://localhost:8080/query/combined | jq

# Get filtered fruits by color
curl http://localhost:8080/query/fruits-color | jq
```

### Configuration Endpoints

```bash
# View current datasource configuration
curl http://localhost:8080/config/datasource | jq

# View current query configuration
curl http://localhost:8080/config/query | jq

# View all environment variables
curl http://localhost:8080/actuator/env | jq
```

### API Documentation

Access the Swagger UI for interactive API documentation:

```
http://localhost:8080/swagger-ui.html
```

### Monitoring Endpoints

```bash
# View all actuator endpoints
curl http://localhost:8080/actuator | jq

# View application metrics
curl http://localhost:8080/actuator/metrics | jq

# Refresh configuration (after changes)
curl -X POST http://localhost:8080/actuator/refresh
```

## Troubleshooting

### Connection Issues

1. **Database Connection Failed**
   - Verify database is running and accessible
   - Check connection credentials in `datasource.yaml`
   - Ensure network connectivity (especially in Docker)

2. **Port Already in Use**
   - Change the port: `--server.port=8081`
   - Or stop the conflicting service

3. **Out of Memory**
   - Increase JVM heap size: `java -Xmx1g -jar query.jar`
   - Adjust Docker container resources

### Logging

Enable debug logging:

```bash
java -jar query.jar --logging.level.sing.app.query=DEBUG
```

Or add to `application.yaml`:

```yaml
logging:
  level:
    "sing.app.query": DEBUG
```

## Additional Resources

- **Docker Hub**: https://hub.docker.com/r/siakhooi/query
- **Helm Chart**: https://artifacthub.io/packages/helm/siakhooi/query
- **GitHub Repository**: https://github.com/siakhooi/query
- **Quality Reports**:
  - SonarCloud: https://sonarcloud.io/project/overview?id=siakhooi_query
  - Qlty: https://qlty.sh/gh/siakhooi/projects/query

## Support

For issues, questions, or contributions, please visit the [GitHub repository](https://github.com/siakhooi/query/issues).
