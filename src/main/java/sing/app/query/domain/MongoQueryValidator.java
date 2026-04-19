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

        String collection = mongoQuery.collection();
        if (collection == null || collection.isBlank()) {
            throw new IllegalArgumentException("MongoDB query requires 'collection'");
        }
    }

    public static boolean shouldUseAggregation(MongoQuery mongoQuery) {
        if (mongoQuery == null) {
            return false;
        }
        return isNotBlank(mongoQuery.pipeline()) ||
               isNotBlank(mongoQuery.filter()) ||
               isNotBlank(mongoQuery.fields()) ||
               isNotBlank(mongoQuery.sort());
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
