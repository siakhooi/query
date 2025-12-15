package sing.app.query.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongodbDatasourceConnection implements DatasourceConnection {

    private MongoClient mongoClient;
    private String databaseName;

    public MongodbDatasourceConnection(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    @Override
    public List<Map<String, Object>> execute(String queryString, String collection, String filter, String fields) {
        log.debug("Executing MongoDB query - collection: {}, filter: {}, fields: {}", collection, filter, fields);

        List<Map<String, Object>> results = new ArrayList<>();

        try {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            if (collection == null || collection.isBlank()) {
                throw new IllegalArgumentException("MongoDB query requires 'collection'");
            }
            MongoCollection<Document> mongoCollection = database.getCollection(collection);

            Document bsonFilter = new Document();
            if (filter != null && !filter.isBlank()) {
                bsonFilter = Document.parse(filter);
            }

            Document projection = new Document();
            if (fields != null && !fields.isBlank()) {
                projection = Document.parse(fields);
            }

            // Execute query and convert to List<Map<String, Object>>
            if (projection.isEmpty()) {
                mongoCollection.find(bsonFilter).forEach(doc -> {
                    Map<String, Object> row = new HashMap<>();
                    doc.forEach((key, value) -> {
                        // Convert ObjectId to String for JSON serialization
                        if (value instanceof org.bson.types.ObjectId) {
                            row.put(key, value.toString());
                        } else {
                            row.put(key, value);
                        }
                    });
                    results.add(row);
                });
            } else {
                mongoCollection.find(bsonFilter).projection(projection).forEach(doc -> {
                    Map<String, Object> row = new HashMap<>();
                    doc.forEach((key, value) -> {
                        // Convert ObjectId to String for JSON serialization
                        if (value instanceof org.bson.types.ObjectId) {
                            row.put(key, value.toString());
                        } else {
                            row.put(key, value);
                        }
                    });
                    results.add(row);
                });
            }

            log.debug("Query returned {} documents", results.size());

        } catch (Exception e) {
            log.error("Error executing MongoDB query: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to execute MongoDB query: " + e.getMessage(), e);
        }

        return results;
    }
}
