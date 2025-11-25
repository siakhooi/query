# User Guide Directory

This directory contains comprehensive documentation and examples for using the Query microservice.

## Contents

- **[QUICKSTART.md](QUICKSTART.md)** - Get up and running in 5 minutes
- **[USAGE.md](USAGE.md)** - Detailed guide covering all deployment methods
- **[docker-compose.yml](docker-compose.yml)** - Complete Docker Compose setup with databases
- **[.env.example](.env.example)** - Environment variable template

## Quick Start

The fastest way to get started:

```bash
cd user-guide
docker-compose up -d
```

This will start:
- MariaDB with sample fruit data
- PostgreSQL with sample animal data
- Query microservice on port 8080

## File Structure

```
user-guide/
├── QUICKSTART.md          # 5-minute quick start guide
├── USAGE.md               # Comprehensive usage documentation
├── docker-compose.yml     # Docker Compose configuration
├── .env.example           # Environment variables template
└── README.md              # This file

../config/                 # Sample configuration files
├── application.yaml       # Main app config
├── datasource.yaml        # Database connections
└── query.yaml             # Query definitions

../sample/                 # Sample SQL data
├── fruit.sql              # MariaDB sample data
└── animal.sql             # PostgreSQL sample data
```

## What You'll Find Here

### QUICKSTART.md
Perfect for first-time users who want to:
- Get the service running quickly
- See it in action with sample data
- Test basic functionality

### USAGE.md
Comprehensive guide that covers:
- Running the JAR file
- Docker deployment
- Docker Compose setup
- Kubernetes/Helm deployment
- Configuration options
- Sample data setup
- API testing and monitoring

### docker-compose.yml
Production-ready Docker Compose file that:
- Sets up MariaDB and PostgreSQL databases
- Configures the Query microservice
- Includes health checks
- Auto-initializes sample data
- Provides network isolation

### .env.example
Template for environment variables:
- Database credentials
- Port configurations
- JVM options
- Logging levels

## Next Steps

1. **Quick Start**: Read [QUICKSTART.md](QUICKSTART.md) to get running in 5 minutes
2. **Deep Dive**: Read [USAGE.md](USAGE.md) for comprehensive documentation
3. **Customize**: Copy `.env.example` to `.env` and adjust settings
4. **Deploy**: Use `docker-compose.yml` for local development or testing

## Support

For issues or questions:
- Check the documentation in this directory
- Visit the [GitHub repository](https://github.com/siakhooi/query)
- Report issues at [GitHub Issues](https://github.com/siakhooi/query/issues)
