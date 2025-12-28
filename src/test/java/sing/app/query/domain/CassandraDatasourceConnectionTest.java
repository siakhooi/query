package sing.app.query.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DriverException;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

import sing.app.query.config.MongoQuery;

@ExtendWith(MockitoExtension.class)
class CassandraDatasourceConnectionTest {

    @Mock
    private CqlSession session;

    @Mock
    private ResultSet resultSet;

    @Mock
    private MongoQuery mongoQuery;

    private CassandraDatasourceConnection connection;

    @BeforeEach
    void setUp() {
        connection = new CassandraDatasourceConnection(session);
    }

    @Test
    void execute_withValidQuery_returnsResults() {
        String query = "SELECT * FROM users";
        Row row1 = createMockRow("id", 1, "name", "Alice");
        Row row2 = createMockRow("id", 2, "name", "Bob");

        when(session.execute(query)).thenReturn(resultSet);
        when(resultSet.spliterator()).thenReturn(Arrays.asList(row1, row2).spliterator());

        List<Map<String, Object>> results = connection.execute(query, mongoQuery);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(1, results.get(0).get("id"));
        assertEquals("Alice", results.get(0).get("name"));
        assertEquals(2, results.get(1).get("id"));
        assertEquals("Bob", results.get(1).get("name"));

        verify(session).execute(query);
    }

    @Test
    void execute_withEmptyResultSet_returnsEmptyList() {
        String query = "SELECT * FROM empty_table";

        when(session.execute(query)).thenReturn(resultSet);
        when(resultSet.spliterator()).thenReturn(Arrays.<Row>asList().spliterator());

        List<Map<String, Object>> results = connection.execute(query, mongoQuery);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(session).execute(query);
    }

    @Test
    void execute_withNullQueryString_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> connection.execute(null, mongoQuery)
        );

        assertEquals("Cassandra queries require a non-empty queryString", exception.getMessage());
    }

    @Test
    void execute_withBlankQueryString_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> connection.execute("   ", mongoQuery)
        );

        assertEquals("Cassandra queries require a non-empty queryString", exception.getMessage());
    }

    @Test
    void execute_withEmptyQueryString_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> connection.execute("", mongoQuery)
        );

        assertEquals("Cassandra queries require a non-empty queryString", exception.getMessage());
    }

    @Test
    void execute_whenDriverExceptionThrown_wrapInCassandraQueryExecutionException() {
        String query = "SELECT * FROM users";
        DriverException driverException = mock(DriverException.class);
        when(driverException.getMessage()).thenReturn("Connection failed");

        when(session.execute(query)).thenThrow(driverException);

        CassandraQueryExecutionException exception = assertThrows(
                CassandraQueryExecutionException.class,
                () -> connection.execute(query, mongoQuery)
        );

        assertTrue(exception.getMessage().contains("Error executing Cassandra query"));
        assertEquals(driverException, exception.getCause());
    }

    @Test
    void execute_maintainsColumnOrder() {
        String query = "SELECT id, name, email, age FROM users";
        Row row = createMockRow("id", 1, "name", "Charlie", "email", "charlie@example.com", "age", 30);

        when(session.execute(query)).thenReturn(resultSet);
        when(resultSet.spliterator()).thenReturn(Arrays.asList(row).spliterator());

        List<Map<String, Object>> results = connection.execute(query, mongoQuery);

        assertNotNull(results);
        assertEquals(1, results.size());

        Map<String, Object> resultRow = results.get(0);
        assertTrue(resultRow instanceof LinkedHashMap, "Result should be a LinkedHashMap to maintain order");

        List<String> keys = List.copyOf(resultRow.keySet());
        assertEquals("id", keys.get(0));
        assertEquals("name", keys.get(1));
        assertEquals("email", keys.get(2));
        assertEquals("age", keys.get(3));
    }

    @Test
    void execute_withNullColumnValues_handlesNullsCorrectly() {
        String query = "SELECT id, name FROM users";
        Row row = createMockRow("id", 1, "name", null);

        when(session.execute(query)).thenReturn(resultSet);
        when(resultSet.spliterator()).thenReturn(Arrays.asList(row).spliterator());

        List<Map<String, Object>> results = connection.execute(query, mongoQuery);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1, results.get(0).get("id"));
        assertEquals(null, results.get(0).get("name"));
    }

    @Test
    void execute_withMultipleRowsAndColumns_mapsAllCorrectly() {
        String query = "SELECT id, name, active FROM users";
        Row row1 = createMockRow("id", 1, "name", "Alice", "active", true);
        Row row2 = createMockRow("id", 2, "name", "Bob", "active", false);
        Row row3 = createMockRow("id", 3, "name", "Charlie", "active", true);

        when(session.execute(query)).thenReturn(resultSet);
        when(resultSet.spliterator()).thenReturn(Arrays.asList(row1, row2, row3).spliterator());

        List<Map<String, Object>> results = connection.execute(query, mongoQuery);

        assertNotNull(results);
        assertEquals(3, results.size());

        assertEquals(1, results.get(0).get("id"));
        assertEquals("Alice", results.get(0).get("name"));
        assertEquals(true, results.get(0).get("active"));

        assertEquals(2, results.get(1).get("id"));
        assertEquals("Bob", results.get(1).get("name"));
        assertEquals(false, results.get(1).get("active"));

        assertEquals(3, results.get(2).get("id"));
        assertEquals("Charlie", results.get(2).get("name"));
        assertEquals(true, results.get(2).get("active"));
    }

    private Row createMockRow(Object... columnNamesAndValues) {
        if (columnNamesAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Must provide pairs of column names and values");
        }

        Row row = mock(Row.class);
        ColumnDefinitions columnDefinitions = mock(ColumnDefinitions.class);

        int columnCount = columnNamesAndValues.length / 2;
        when(columnDefinitions.size()).thenReturn(columnCount);

        for (int i = 0; i < columnCount; i++) {
            String columnName = (String) columnNamesAndValues[i * 2];
            Object value = columnNamesAndValues[i * 2 + 1];

            ColumnDefinition columnDefinition = mock(ColumnDefinition.class);
            com.datastax.oss.driver.api.core.CqlIdentifier identifier =
                    mock(com.datastax.oss.driver.api.core.CqlIdentifier.class);

            when(identifier.asInternal()).thenReturn(columnName);
            when(columnDefinition.getName()).thenReturn(identifier);
            when(columnDefinitions.get(i)).thenReturn(columnDefinition);
            when(row.getObject(i)).thenReturn(value);
        }

        when(row.getColumnDefinitions()).thenReturn(columnDefinitions);

        return row;
    }
}
