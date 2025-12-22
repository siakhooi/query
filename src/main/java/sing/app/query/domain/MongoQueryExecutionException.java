package sing.app.query.domain;

/**
 * Exception thrown when MongoDB query execution fails.
 */
public class MongoQueryExecutionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MongoQueryExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
