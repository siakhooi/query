help:
# make all     : build
# make clean   : clean
# make git-push: commit and push
# make release : create release

# make test	    : run tests
# make run	    : run the application
# make curl-env : test endpoints

# make docker-run: run docker image
# make curl-env  : test endpoints

# k3d-up
# make helm-install
# make k-pf
# make curl-env


all: clean setversion build docker-build helm-build

clean:
	mvn clean
	rm -f query-*.tgz query-release-1.chart.yaml query-*.jar
setversion:
	bin/update-versions.sh
build:
	mvn verify
docker-build:
	bin/docker-build.sh
commit:
	bin/git-commit-and-push.sh
release:
	bin/create-release.sh

test:
	mvn test
run:
	mvn spring-boot:run

curl-env:
	curl -s http://localhost:8080/actuator/env | jq | tee env.json
curl-env-1:
	curl -s http://localhost:8080/actuator/env/spring.cloud.kubernetes.enabled |jq
curl-env-2:
	curl -s http://localhost:8080/actuator/env/spring.cloud.kubernetes.enabled |jq
curl-env-3:
#	curl -s http://localhost:8080/actuator/env/query.querysets[0].name |jq
	curl -s http://localhost:8080/actuator/env/query.querysets%5B0%5D.name |jq|hl 'query-query'
curl-configprops:
	curl -s http://localhost:8080/actuator/configprops|jq '.contexts.application.beans.greetingConfig'
curl-refresh:
	curl -X POST http://localhost:8080/actuator/refresh

curl-config-query:
	curl --no-progress-meter http://localhost:8080/config/query |jq

curl-config-datasource:
	curl --no-progress-meter http://localhost:8080/config/datasource |jq

curl-q1:
	curl --no-progress-meter http://localhost:8080/query/fruits |jq
curl-q2:
	curl --no-progress-meter http://localhost:8080/query/fruits-color |jq
curl-q3:
	curl --no-progress-meter http://localhost:8080/query/animals |jq
curl-q4:
	curl --no-progress-meter http://localhost:8080/query/all |jq

secrets:
#	datasource: ZGF0YXNvdXJjZToKICBjb25uZWN0aW9uczoKICAtIG5hbWU6IGluLW1lbW9yeQogICAgdXJsOiBqZGJjOmgyOm1lbTp0ZXN0ZGI7REJfQ0xPU0VfREVMQVk9LTE7REJfQ0xPU0VfT05fRVhJVD1GQUxTRQ==
#   datasource: ZGF0YXNvdXJjZToKICBjb25uZWN0aW9uczoKICAtIG5hbWU6IGluLW1lbW9yeQogICAgdXJsOiBqZGJjOmgyOm1lbTp0ZXN0ZGI7REJfQ0xPU0VfREVMQVk9LTE7REJfQ0xPU0VfT05fRVhJVD1UUlVF

docker-run:
	docker run --rm -p 8080:8080 siakhooi/query:latest
# docker-inspect:
# 	docker inspect siakhooi/query:latest
# docker-get-base-digest:
# 	docker inspect eclipse-temurin:21.0.2_13-jre-alpine | jq -r '.[].Id'
# curl-actuator:
# 	curl --no-progress-meter http://localhost:8080/actuator |jq
# curl-actuator-health:
# 	curl http://localhost:8080/actuator/health
# curl-actuator-shutdown:
# 	curl -X POST http://localhost:8080/actuator/shutdown
# curl-springdoc-api:
# 	curl --no-progress-meter http://localhost:8080/v3/api-docs
# curl-springdoc-api-ui:
# 	curl --no-progress-meter http://localhost:8080/swagger-ui.html

# helm-create:
# 	mkdir -p deploy/helm
# 	cd deploy/helm
# 	helm create query

helm-lint:
	helm lint deploy/helm/query/
helm-template:
	helm template  query-release-1  deploy/helm/query/ --debug | tee query-release-1.chart.yaml
helm-package:
	helm package   deploy/helm/query/
helm-unittest:
	bin/helm-unit-test.sh
helm-build: helm-lint helm-template helm-unittest helm-package

# helm-reinstall: helm-uninstall zootopia-load-image helm-install
helm-install: k3d-load-image
	shed-helm install hello1 ./query-0.6.0.tgz
helm-uninstall:
	shed-helm uninstall hello1

k-pf:
	shed-kubectl port-forward service/hello1-query 8080:80
# k-create-configmap:
# 	k create configmap hello-spring-boot-microservice --from-literal=app.defaultGreetingMessage=Wakanda
# k-get-cm:
# 	shed-kubectl get cm/query -o yaml
# k-edit-cm:
# 	shed-kubectl edit cm/query

k3d-load-image:
	k3d-image-import siakhooi/query:0.5.0

zootopia-load-image:
 	zootopia-load-docker-images siakhooi/query:latest
