FROM eclipse-temurin:21-jre-alpine
LABEL org.opencontainers.image.authors="Siak Hooi, Ng"
LABEL org.opencontainers.image.vendor="Siak Hooi, Ng"
LABEL org.opencontainers.image.url=https://hub.docker.com/r/siakhooi/query
LABEL org.opencontainers.image.documentation=https://github.com/siakhooi/query
LABEL org.opencontainers.image.source=https://github.com/siakhooi/query
LABEL org.opencontainers.image.title=query
LABEL org.opencontainers.image.description="Query"
LABEL org.opencontainers.image.licenses=MIT
LABEL org.opencontainers.image.base.name=21-jre-alpine
LABEL org.opencontainers.image.base.digest=sha256:e7ed585b34913e0a780e0282330183a0ea14ad6b929362d02aea1156b43262bf
LABEL org.opencontainers.image.version=0.18.0
LABEL org.opencontainers.image.revision=0.19.0
LABEL org.opencontainers.image.created="2025-12-27 14:18:56.944273480+00:00"

ARG APP_VERSION=0.18.0
ARG JAR_NAME=query

WORKDIR /app

EXPOSE 8080

RUN apk add --no-cache curl

COPY --chown=root:root --chmod=644 ${JAR_NAME}-${APP_VERSION}.jar query.jar

RUN addgroup -g 10001 group00 && adduser -D -u 10002 user00 group00
USER user00:group00

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 CMD curl -fsS http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/query.jar"]
