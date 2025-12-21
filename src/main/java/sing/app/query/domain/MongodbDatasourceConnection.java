package sing.app.query.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import sing.app.query.config.MongoQuery;

@Slf4j
public class MongodbDatasourceConnection implements DatasourceConnection {

    private MongoClient mongoClient;
    private String databaseName;

    public MongodbDatasourceConnection(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    @Override
    public List<Map<String, Object>> execute(String queryString, MongoQuery mongoQuery) {
        String collection = mongoQuery != null ? mongoQuery.getCollection() : null;
        String filter = mongoQuery != null ? mongoQuery.getFilter() : null;
        String fields = mongoQuery != null ? mongoQuery.getFields() : null;
        String sort = mongoQuery != null ? mongoQuery.getSort() : null;
        String pipeline = mongoQuery != null ? mongoQuery.getPipeline() : null;

        log.debug("Executing MongoDB query - collection: {}, filter: {}, fields: {}, sort: {}, pipeline: {}",
                collection, filter, fields, sort, pipeline);

        List<Map<String, Object>> results = new ArrayList<>();

        try {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            if (collection == null || collection.isBlank()) {
                throw new IllegalArgumentException("MongoDB query requires 'collection'");
            }
            MongoCollection<Document> mongoCollection = database.getCollection(collection);

            // Check if we need to use aggregation pipeline
            boolean hasPipeline = pipeline != null && !pipeline.isBlank();
            boolean hasFilter = filter != null && !filter.isBlank();
            boolean hasFields = fields != null && !fields.isBlank();
            boolean hasSort = sort != null && !sort.isBlank();

            if (hasPipeline || hasFilter || hasFields || hasSort) {
                // Build combined aggregation pipeline
                List<Document> stages = new ArrayList<>();

                // 1. Add filter as $match stage
                if (hasFilter) {
                    stages.add(new Document("$match", Document.parse(filter)));
                }

                // 2. Add custom pipeline stages
                if (hasPipeline) {
                    List<Document> customStages = parsePipelineArray(pipeline);
                    stages.addAll(customStages);
                }

                // 3. Add fields as $project stage
                if (hasFields) {
                    stages.add(new Document("$project", Document.parse(fields)));
                }

                // 4. Add sort as $sort stage
                if (hasSort) {
                    stages.add(new Document("$sort", Document.parse(sort)));
                }

                // Execute aggregation pipeline
                mongoCollection.aggregate(stages).forEach(doc -> {
                    Map<String, Object> row = new HashMap<>();
                    doc.forEach((key, value) -> {
                        // Convert ObjectId to String for JSON serialization
                        if (value instanceof ObjectId) {
                            row.put(key, value.toString());
                        } else {
                            row.put(key, value);
                        }
                    });
                    results.add(row);
                });
            } else {
                // Use simple find query for backward compatibility
                mongoCollection.find().forEach(doc -> {
                    Map<String, Object> row = new HashMap<>();
                    doc.forEach((key, value) -> {
                        // Convert ObjectId to String for JSON serialization
                        if (value instanceof ObjectId) {
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

    private List<Document> parsePipelineArray(String pipeline) {
        List<Document> stages = new ArrayList<>();
        // Parse the pipeline string as a JSON array
        BsonArray bsonArray = BsonArray.parse(pipeline);
        for (BsonValue value : bsonArray) {
            stages.add(Document.parse(value.asDocument().toJson()));
        }
        return stages;
    }
}
