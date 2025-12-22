package sing.app.query.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.MongoException;
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
        logQueryExecution(mongoQuery);

        try {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            String collection = getCollection(mongoQuery);
            validateCollection(collection);
            MongoCollection<Document> mongoCollection = database.getCollection(collection);

            List<Map<String, Object>> results = shouldUseAggregation(mongoQuery)
                    ? executeAggregation(mongoCollection, mongoQuery)
                    : executeSimpleFind(mongoCollection);

            log.debug("Query returned {} documents", results.size());
            return results;

        } catch (MongoException e) {
            log.error("MongoDB driver error executing query: {}", e.getMessage(), e);
            throw new MongoQueryExecutionException("Failed to execute MongoDB query: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid MongoDB query configuration: {}", e.getMessage(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Unexpected error executing MongoDB query: {}", e.getMessage(), e);
            throw new MongoQueryExecutionException("Unexpected failure during MongoDB query execution", e);
        }
    }

    private String getCollection(MongoQuery mongoQuery) {
        return mongoQuery != null ? mongoQuery.getCollection() : null;
    }

    private void logQueryExecution(MongoQuery mongoQuery) {
        if (mongoQuery != null) {
            log.debug("Executing MongoDB query - collection: {}, filter: {}, fields: {}, sort: {}, pipeline: {}",
                    mongoQuery.getCollection(), mongoQuery.getFilter(), mongoQuery.getFields(),
                    mongoQuery.getSort(), mongoQuery.getPipeline());
        }
    }

    private void validateCollection(String collection) {
        if (collection == null || collection.isBlank()) {
            throw new IllegalArgumentException("MongoDB query requires 'collection'");
        }
    }

    private boolean shouldUseAggregation(MongoQuery mongoQuery) {
        if (mongoQuery == null) {
            return false;
        }
        return isNotBlank(mongoQuery.getPipeline()) || isNotBlank(mongoQuery.getFilter())
                || isNotBlank(mongoQuery.getFields()) || isNotBlank(mongoQuery.getSort());
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private List<Map<String, Object>> executeAggregation(MongoCollection<Document> mongoCollection,
                                                          MongoQuery mongoQuery) {
        List<Document> stages = buildAggregationPipeline(mongoQuery);
        List<Map<String, Object>> results = new ArrayList<>();
        mongoCollection.aggregate(stages).forEach(doc -> results.add(convertDocumentToMap(doc)));
        return results;
    }

    private List<Map<String, Object>> executeSimpleFind(MongoCollection<Document> mongoCollection) {
        List<Map<String, Object>> results = new ArrayList<>();
        mongoCollection.find().forEach(doc -> results.add(convertDocumentToMap(doc)));
        return results;
    }

    private List<Document> buildAggregationPipeline(MongoQuery mongoQuery) {
        List<Document> stages = new ArrayList<>();

        if (isNotBlank(mongoQuery.getFilter())) {
            stages.add(new Document("$match", Document.parse(mongoQuery.getFilter())));
        }

        if (isNotBlank(mongoQuery.getPipeline())) {
            stages.addAll(parsePipelineArray(mongoQuery.getPipeline()));
        }

        if (isNotBlank(mongoQuery.getFields())) {
            stages.add(new Document("$project", Document.parse(mongoQuery.getFields())));
        }

        if (isNotBlank(mongoQuery.getSort())) {
            stages.add(new Document("$sort", Document.parse(mongoQuery.getSort())));
        }

        return stages;
    }

    private Map<String, Object> convertDocumentToMap(Document doc) {
        Map<String, Object> row = new HashMap<>();
        doc.forEach((key, value) -> {
            if (value instanceof ObjectId) {
                row.put(key, value.toString());
            } else {
                row.put(key, value);
            }
        });
        return row;
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
