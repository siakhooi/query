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
LABEL org.opencontainers.image.version=0.16.0
LABEL org.opencontainers.image.revision=0.17.0
LABEL org.opencontainers.image.created="2025-12-21 08:40:44.690621516+00:00"

ARG APP_VERSION=0.16.0
ARG JAR_NAME=query

EXPOSE 8080

COPY --chown=root:root --chmod=644 ${JAR_NAME}-${APP_VERSION}.jar query.jar

RUN addgroup -g 10001 group00 && adduser -D -u 10002 user00 group00
USER user00:group00

ENTRYPOINT ["java","-jar","/query.jar"]
