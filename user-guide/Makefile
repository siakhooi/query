up:
	docker compose up | tee compose-up.log
clean:
	docker volume rm user-guide_mariadb_data user-guide_mongodb_data user-guide_postgres_data

clean-json:
	rm -f *.json

curl-all: clean-json
	curl http://localhost:8080/actuator/health| jq | tee health.json

	# Query fruits
	curl http://localhost:8080/query/fruits | jq | tee fruits.json
	curl http://localhost:8080/query/fruits-color | jq | tee fruits-color.json

	# Query animals
	curl http://localhost:8080/query/animals | jq | tee animals.json

	# Query books
	curl http://localhost:8080/query/books | jq | tee books.json
	curl http://localhost:8080/query/books-fiction | jq | tee books-fiction.json
	curl http://localhost:8080/query/books-selected-fields | jq | tee books-selected-fields.json
	curl http://localhost:8080/query/books-sorted-by-title | jq | tee books-sorted-by-title.json
	curl http://localhost:8080/query/books-sorted-multi | jq | tee books-sorted-multi.json
	curl http://localhost:8080/query/books-fiction-sorted | jq | tee books-fiction-sorted.json
	curl http://localhost:8080/query/books-aggregation | jq | tee books-aggregation.json

	# Query metrics
	curl http://localhost:8080/query/metrics | jq | tee metrics.json

	# Get combined results
	curl http://localhost:8080/query/all | jq | tee all.json
	curl http://localhost:8080/query/statistics | jq | tee statistics.json
