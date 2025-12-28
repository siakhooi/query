package sing.app.query.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DriverException;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sing.app.query.config.MongoQuery;

/**
 * Datasource connection for executing Cassandra CQL queries.
 */
@Slf4j
@RequiredArgsConstructor
public class CassandraDatasourceConnection implements DatasourceConnection {

    private final CqlSession session;

    /**
     * Executes a Cassandra CQL query and returns the results.
     *
     * @param queryString the CQL query to execute
     * @param mongoQuery unused for Cassandra queries; required by the DatasourceConnection interface
     * @return a list of maps representing the query results
     * @throws IllegalArgumentException if queryString is null or blank
     * @throws CassandraQueryExecutionException if query execution fails
     */
    @Override
    public List<Map<String, Object>> execute(String queryString, MongoQuery mongoQuery) {
        if (queryString == null || queryString.isBlank()) {
            throw new IllegalArgumentException("Cassandra queries require a non-empty queryString");
        }

        log.debug("Executing Cassandra query: {}", queryString);

        try {
            ResultSet resultSet = session.execute(queryString);
            List<Map<String, Object>> results = new ArrayList<>();

            for (Row row : resultSet) {
                results.add(mapRow(row));
            }

            log.debug("Cassandra query returned {} rows", results.size());
            return results;

        } catch (DriverException e) {
            String errorMessage = "Error executing Cassandra query: " + e.getMessage();
            log.error(errorMessage, e);
            throw new CassandraQueryExecutionException(errorMessage, e);
        }
    }

    private Map<String, Object> mapRow(Row row) {
        Map<String, Object> mappedRow = new LinkedHashMap<>();
        ColumnDefinitions columnDefinitions = row.getColumnDefinitions();

        for (int i = 0; i < columnDefinitions.size(); i++) {
            String columnName = columnDefinitions.get(i).getName().asInternal();
            Object value = row.getObject(i);
            mappedRow.put(columnName, value);
        }

        return mappedRow;
    }
}
