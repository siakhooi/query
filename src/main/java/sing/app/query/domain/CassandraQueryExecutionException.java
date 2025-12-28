package sing.app.query.domain;

/**
 * Exception thrown when Cassandra query execution fails.
 */
public class CassandraQueryExecutionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CassandraQueryExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
