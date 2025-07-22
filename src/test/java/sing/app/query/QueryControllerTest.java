package sing.app.query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class QueryControllerTest {

    private GreetingConfig greetingConfig;
    private QueryConfig queryConfig;
    private DatasourceConfig datasourceConfig;
    private QueryController controller;

    @BeforeEach
    void setUp() {
        greetingConfig = mock(GreetingConfig.class);
        queryConfig = mock(QueryConfig.class);
        datasourceConfig = mock(DatasourceConfig.class);
        controller = new QueryController(greetingConfig, queryConfig, datasourceConfig);
    }

    @Test
    void getQueryConfig_returnsDeepCopyOfQueryConfig() throws JsonProcessingException{
        // Arrange
        String json = """
                [
                  {
                    "name": "set1",
                    "queries": [
                      {
                    "name": "q1",
                    "connection": "conn1"
                      }
                    ]
                  },
                  {
                    "name": "set2",
                    "queries": [
                      {
                    "name": "q2",
                    "connection": "conn2"
                      }
                    ]
                  }
                ]
                """;

        ObjectMapper mapper = new ObjectMapper();
        List<QueryConfig.Queryset> querysets = mapper.readValue(
                json, mapper.getTypeFactory().constructCollectionType(List.class, QueryConfig.Queryset.class));

        when(queryConfig.getQuerysets()).thenReturn(querysets);

        // Act
        QueryConfig result = controller.getQueryConfig();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getQuerysets().size());

        QueryConfig.Queryset resultSet1 = result.getQuerysets().get(0);
        assertEquals("set1", resultSet1.getName());
        assertEquals(1, resultSet1.getQueries().size());
        assertEquals("q1", resultSet1.getQueries().get(0).getName());
        assertEquals("conn1", resultSet1.getQueries().get(0).getConnection());

        QueryConfig.Queryset resultSet2 = result.getQuerysets().get(1);
        assertEquals("set2", resultSet2.getName());
        assertEquals(1, resultSet2.getQueries().size());
        assertEquals("q2", resultSet2.getQueries().get(0).getName());
        assertEquals("conn2", resultSet2.getQueries().get(0).getConnection());
    }

    @Test
    void getQueryConfig_handlesEmptyQuerysets() {
        when(queryConfig.getQuerysets()).thenReturn(List.of());

        QueryConfig result = controller.getQueryConfig();

        assertNotNull(result);
        assertTrue(result.getQuerysets().isEmpty());
    }
}