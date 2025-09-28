package sing.app.query.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MariadbDatasourceConnectionTest {

    private DataSource mockDataSource;
    private JdbcTemplate mockJdbcTemplate;
    private MariadbDatasourceConnection connection;

    @BeforeEach
    void setUp() {
        mockDataSource = mock(DataSource.class);
        mockJdbcTemplate = mock(JdbcTemplate.class);
        // Use reflection to inject mockJdbcTemplate
        connection = new MariadbDatasourceConnection(mockDataSource);
        try {
            var field = MariadbDatasourceConnection.class.getDeclaredField("jdbcTemplate");
            field.setAccessible(true);
            field.set(connection, mockJdbcTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testExecuteReturnsQueryResults() {
        String query = "SELECT * FROM test";
        List<Map<String, Object>> expected = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 1);
        row.put("name", "Alice");
        expected.add(row);

        when(mockJdbcTemplate.queryForList(query)).thenReturn(expected);

        List<Map<String, Object>> result = connection.execute(query);

        assertEquals(expected, result);
        verify(mockJdbcTemplate, times(1)).queryForList(query);
    }

    @Test
    void testExecuteWithEmptyResult() {
        String query = "SELECT * FROM empty_table";
        when(mockJdbcTemplate.queryForList(query)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = connection.execute(query);

        assertTrue(result.isEmpty());
        verify(mockJdbcTemplate).queryForList(query);
    }

    @Test
    void testExecuteThrowsException() {
        String query = "SELECT * FROM invalid";
        when(mockJdbcTemplate.queryForList(query)).thenThrow(new RuntimeException("DB error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> connection.execute(query));
        assertEquals("DB error", thrown.getMessage());
        verify(mockJdbcTemplate).queryForList(query);
    }
}