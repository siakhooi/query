package sing.app.query.domain;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.BsonArray;
import org.bson.BsonValue;
import sing.app.query.config.MongoQuery;

public class MongoPipelineBuilder {
    private final MongoQuery mongoQuery;

    public MongoPipelineBuilder(MongoQuery mongoQuery) {
        this.mongoQuery = mongoQuery;
    }

    public List<Document> build() {
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

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private List<Document> parsePipelineArray(String pipeline) {
        List<Document> stages = new ArrayList<>();
        BsonArray bsonArray = BsonArray.parse(pipeline);
        for (BsonValue value : bsonArray) {
            stages.add(Document.parse(value.asDocument().toJson()));
        }
        return stages;
    }
}
