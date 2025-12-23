package sing.app.query.domain;

import sing.app.query.config.MongoQuery;

public class MongoQueryValidator {

    private MongoQueryValidator() {
        // Private constructor to prevent instantiation
    }

    public static void validateQuery(MongoQuery mongoQuery) {
        if (mongoQuery == null) {
            throw new IllegalArgumentException("MongoQuery cannot be null");
        }

        String collection = mongoQuery.getCollection();
        if (collection == null || collection.isBlank()) {
            throw new IllegalArgumentException("MongoDB query requires 'collection'");
        }
    }

    public static boolean shouldUseAggregation(MongoQuery mongoQuery) {
        if (mongoQuery == null) {
            return false;
        }
        return isNotBlank(mongoQuery.getPipeline()) ||
               isNotBlank(mongoQuery.getFilter()) ||
               isNotBlank(mongoQuery.getFields()) ||
               isNotBlank(mongoQuery.getSort());
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
