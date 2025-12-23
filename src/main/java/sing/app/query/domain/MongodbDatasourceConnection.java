package sing.app.query.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sing.app.query.config.MongoQuery;
import sing.app.query.domain.util.MongoDocumentMapper;

@Slf4j
@RequiredArgsConstructor
public class MongodbDatasourceConnection implements DatasourceConnection {

    private final MongoClient mongoClient;
    private final String databaseName;

    @Override
    public List<Map<String, Object>> execute(String queryString, MongoQuery mongoQuery) {
        logQueryExecution(mongoQuery);
        MongoQueryValidator.validateQuery(mongoQuery);

        try {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            String collectionName = mongoQuery.getCollection();
            MongoCollection<Document> mongoCollection = database.getCollection(collectionName);

            List<Map<String, Object>> results = MongoQueryValidator.shouldUseAggregation(mongoQuery)
                    ? executeAggregation(mongoCollection, mongoQuery)
                    : executeSimpleFind(mongoCollection);

            log.debug("Query returned {} documents", results.size());
            return results;

        } catch (MongoException e) {
            String errorMessage = "MongoDB driver error executing query: " + e.getMessage();
            log.error(errorMessage, e);
            throw new MongoQueryExecutionException(errorMessage, e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid MongoDB query configuration: {}", e.getMessage(), e);
            throw e;
        } catch (RuntimeException e) {
            String errorMessage = "Unexpected error executing MongoDB query: " + e.getMessage();
            log.error(errorMessage, e);
            throw new MongoQueryExecutionException(errorMessage, e);
        }
    }

    private void logQueryExecution(MongoQuery mongoQuery) {
        if (mongoQuery != null) {
            log.debug("Executing MongoDB query - collection: {}, filter: {}, fields: {}, sort: {}, pipeline: {}",
                    mongoQuery.getCollection(), mongoQuery.getFilter(), mongoQuery.getFields(),
                    mongoQuery.getSort(), mongoQuery.getPipeline());
        }
    }

    private List<Map<String, Object>> executeAggregation(MongoCollection<Document> mongoCollection,
                                                         MongoQuery mongoQuery) {
        List<Document> stages = new MongoPipelineBuilder(mongoQuery).build();
        List<Map<String, Object>> results = new ArrayList<>();
        mongoCollection.aggregate(stages).forEach(doc -> results.add(MongoDocumentMapper.toMap(doc)));
        return results;
    }

    private List<Map<String, Object>> executeSimpleFind(MongoCollection<Document> mongoCollection) {
        List<Map<String, Object>> results = new ArrayList<>();
        mongoCollection.find().forEach(doc -> results.add(MongoDocumentMapper.toMap(doc)));
        return results;
    }
}
