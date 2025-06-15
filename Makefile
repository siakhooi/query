help:
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
git-push:
	bin/git-commit-and-push.sh
create-release:
	bin/create-release.sh

test:
	mvn test
run:
	mvn spring-boot:run
gh-rerun:
	gh run rerun --failed

curl:
	curl http://localhost:8080/greeting
curl-env:
	curl -s http://localhost:8080/actuator/env | jq> env.json
#	curl -s http://localhost:8080/actuator/env | jq -r '.propertySources[].properties."app.defaultGreetingMessage".value|select\(.!=null\)'
curl-configprops:
	curl -s http://localhost:8080/actuator/configprops|jq '.contexts.application.beans.greetingConfig'

# run-moon:
# 	java -jar -Dapp.defaultGreetingMessage=Moon target/query-0.6.0.jar
# run-jupiter:
# 	app_defaultGreetingMessage=Jupiter java -jar target/query-0.6.0.jar

# delete-release:
# 	gh release delete --cleanup-tag 0.25.0
# docker-run:
# 	docker run --rm -p 8080:8080 siakhooi/query:latest
# docker-run-uranus:
# 	docker run --rm -p 8080:8080 -e app_defaultGreetingMessage=Uranus siakhooi/query:latest
# docker-run-base:
# 	docker run -it --rm eclipse-temurin:21.0.2_13-jre-alpine sh
# docker-inspect:
# 	docker inspect siakhooi/query:latest
# docker-get-base-digest:
# 	docker inspect eclipse-temurin:21.0.2_13-jre-alpine | jq -r '.[].Id'
# curl-earth:
# 	curl http://localhost:8080/greeting?name=Earth
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
# helm-uninstall:
# 	shed-helm uninstall hello1
# helm-install: zootopia-load-image
# 	shed-helm install hello1 ./query-0.24.0.tgz

# k-pf:
# 	shed-kubectl port-forward service/hello1-query 8080:80
# k-create-configmap:
# 	k create configmap hello-spring-boot-microservice --from-literal=app.defaultGreetingMessage=Wakanda
# k-get-cm:
# 	shed-kubectl get cm/query -o yaml
# k-edit-cm:
# 	shed-kubectl edit cm/query

# zootopia-load-image:
# 	zootopia-load-docker-images siakhooi/query:0.18.0
