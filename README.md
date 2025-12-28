# query

microservice to query backend databases
- configurable data connections and queries to query various backend databases and return as 1 json content.
- Currently supported databases:
  - mariadb
  - postgresql
  - mongodb
  - cassandra

## Documentation

🚀 **[Quick Start](user-guide/QUICKSTART.md)** - Get up and running in 5 minutes

📚 **[Usage Guide](user-guide/USAGE.md)** - Complete guide on how to run and configure the Query microservice:
- Running the JAR
- Running with Docker
- Running with Docker Compose
- Running with Helm Chart
- Configuration examples
- Sample data setup

## Reference:

### Deliverables

- https://artifacthub.io/packages/helm/siakhooi/query
- https://hub.docker.com/r/siakhooi/query
- https://siakhooi.github.io/helm-charts

### Quality

- https://sonarcloud.io/project/overview?id=siakhooi_query
- https://qlty.sh/gh/siakhooi/projects/query

### Base Images

- https://hub.docker.com/_/eclipse-temurin/tags?page=&page_size=&ordering=&name=jre-alpine

## Badges

![GitHub](https://img.shields.io/github/license/siakhooi/query?logo=github)
![GitHub last commit](https://img.shields.io/github/last-commit/siakhooi/query?logo=github)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/siakhooi/query?logo=github)
![GitHub issues](https://img.shields.io/github/issues/siakhooi/query?logo=github)
![GitHub closed issues](https://img.shields.io/github/issues-closed/siakhooi/query?logo=github)
![GitHub pull requests](https://img.shields.io/github/issues-pr-raw/siakhooi/query?logo=github)
![GitHub closed pull requests](https://img.shields.io/github/issues-pr-closed-raw/siakhooi/query?logo=github)
![GitHub top language](https://img.shields.io/github/languages/top/siakhooi/query?logo=github)
![GitHub language count](https://img.shields.io/github/languages/count/siakhooi/query?logo=github)
![Lines of code](https://img.shields.io/tokei/lines/github/siakhooi/query?logo=github)
![GitHub repo size](https://img.shields.io/github/repo-size/siakhooi/query?logo=github)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/siakhooi/query?logo=github)
![Workflow](https://img.shields.io/badge/Workflow-github-purple)
![workflow](https://github.com/siakhooi/query/actions/workflows/workflow-build-on-push.yml/badge.svg)
![workflow](https://github.com/siakhooi/query/actions/workflows/workflow-deployments.yml/badge.svg)

![Release](https://img.shields.io/badge/Release-github-purple)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/siakhooi/query?label=GPR%20release&logo=github)
![GitHub all releases](https://img.shields.io/github/downloads/siakhooi/query/total?color=33cb56&logo=github)
![GitHub Release Date](https://img.shields.io/github/release-date/siakhooi/query?logo=github)

![Quality-Qlty](https://img.shields.io/badge/Quality-Qlty-purple)
[![Maintainability](https://qlty.sh/gh/siakhooi/projects/query/maintainability.svg)](https://qlty.sh/gh/siakhooi/projects/query)
[![Code Coverage](https://qlty.sh/gh/siakhooi/projects/query/coverage.svg)](https://qlty.sh/gh/siakhooi/projects/query)

![Quality-Sonar](https://img.shields.io/badge/Quality-SonarCloud-purple)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_query&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=siakhooi_query)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_query&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=siakhooi_query)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_query&metric=bugs)](https://sonarcloud.io/summary/new_code?id=siakhooi_query)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_query&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=siakhooi_query)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_query&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=siakhooi_query)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_query&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=siakhooi_query)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_query&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=siakhooi_query)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_query&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=siakhooi_query)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_query&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=siakhooi_query)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_query&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=siakhooi_query)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_query&metric=coverage)](https://sonarcloud.io/summary/new_code?id=siakhooi_query)
![Sonar Violations (short format)](https://img.shields.io/sonar/violations/siakhooi_query?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (short format)](https://img.shields.io/sonar/blocker_violations/siakhooi_query?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (short format)](https://img.shields.io/sonar/critical_violations/siakhooi_query?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (short format)](https://img.shields.io/sonar/major_violations/siakhooi_query?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (short format)](https://img.shields.io/sonar/minor_violations/siakhooi_query?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (short format)](https://img.shields.io/sonar/info_violations/siakhooi_query?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (long format)](https://img.shields.io/sonar/violations/siakhooi_query?format=long&server=http%3A%2F%2Fsonarcloud.io)

[![Generic badge](https://img.shields.io/badge/Funding-BuyMeACoffee-33cb56.svg)](https://www.buymeacoffee.com/siakhooi)
[![Generic badge](https://img.shields.io/badge/Funding-Ko%20Fi-33cb56.svg)](https://ko-fi.com/siakhooi)
![visitors](https://hit-tztugwlsja-uc.a.run.app/?outputtype=badge&counter=ghmd-query)
