package sing.app.query.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import sing.app.query.config.DatasourceConfig;
import sing.app.query.config.DatasourceConfig.Connection;
import sing.app.query.config.DatasourceConfigException;
import sing.app.query.config.MongoQuery;
import sing.app.query.config.QueryConfig;
import sing.app.query.config.QueryConfig.Query;
import sing.app.query.domain.JdbcDatasourceConnection;
import sing.app.query.service.DatasourceConnectionService;

@ExtendWith(MockitoExtension.class)
class QueryControllerTest {

    private static final MongoQuery MONGO_Q = new MongoQuery("books", null, null, null, null);
    private static final Query JDBC_QUERY = new Query("fruits", "SELECT 1", null, "fruitdb");
    private static final Query MONGO_QUERY = new Query("books", null, MONGO_Q, "bookdb");

    @Mock
    private QueryConfig queryConfig;
    @Mock
    private DatasourceConfig datasourceConfig;
    @Mock
    private DatasourceConnectionService dcs;
    @Mock
    private Connection jdbcConnection;
    @Mock
    private Connection mongoConnection;

    private QueryController controller;

    @BeforeEach
    void setUp() {
        controller = new QueryController(queryConfig, datasourceConfig, dcs);
        lenient().when(jdbcConnection.getType()).thenReturn("jdbc");
        lenient().when(mongoConnection.getType()).thenReturn("mongodb");
    }

    @AfterEach
    void clearInterrupt() {
        Thread.interrupted();
    }

    @Test
    void getQuery_executesJdbcBranch() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        JdbcDatasourceConnection jdbcDc = spy(new JdbcDatasourceConnection(dataSource));
        doReturn(List.of(Map.of("a", 1))).when(jdbcDc).execute(eq("SELECT 1"), isNull());

        when(queryConfig.getQueries("q")).thenReturn(List.of(JDBC_QUERY));
        when(datasourceConfig.getConnection("fruitdb", "fruits")).thenReturn(jdbcConnection);
        when(dcs.getConnection(jdbcConnection)).thenReturn(jdbcDc);

        Map<String, List<Map<String, Object>>> out = controller.getQuery("q");

        assertEquals(1, out.get("fruits").size());
        verify(jdbcDc).execute(eq("SELECT 1"), isNull());
        verify(jdbcDc, never()).execute(isNull(), any());
    }

    @Test
    void getQuery_executesMongodbBranch() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        JdbcDatasourceConnection jdbcDc = spy(new JdbcDatasourceConnection(dataSource));
        doReturn(List.of()).when(jdbcDc).execute(isNull(), eq(MONGO_Q));

        when(queryConfig.getQueries("q")).thenReturn(List.of(MONGO_QUERY));
        when(datasourceConfig.getConnection("bookdb", "books")).thenReturn(mongoConnection);
        when(dcs.getConnection(mongoConnection)).thenReturn(jdbcDc);

        Map<String, List<Map<String, Object>>> out = controller.getQuery("q");

        assertTrue(out.containsKey("books"));
        verify(jdbcDc).execute(isNull(), eq(MONGO_Q));
        verify(jdbcDc, never()).execute(anyString(), isNull());
    }

    @Test
    void getQuery_wrapsDatasourceConfigFailureInResponseStatusException() throws Exception {
        when(queryConfig.getQueries("q")).thenReturn(List.of(JDBC_QUERY));
        doThrow(new DatasourceConfigException("down")).when(datasourceConfig).getConnection("fruitdb", "fruits");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.getQuery("q"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Error fetching connection"));
    }

    @Test
    void getQuery_propagatesResponseStatusExceptionFromTask() throws Exception {
        when(queryConfig.getQueries("q")).thenReturn(List.of(JDBC_QUERY));
        when(datasourceConfig.getConnection(any(), any())).thenReturn(jdbcConnection);
        ResponseStatusException notFound = new ResponseStatusException(HttpStatus.NOT_FOUND, "missing");
        when(dcs.getConnection(jdbcConnection)).thenThrow(notFound);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.getQuery("q"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getQuery_propagatesRuntimeExceptionFromTask() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        JdbcDatasourceConnection jdbcDc = spy(new JdbcDatasourceConnection(dataSource));
        doThrow(new IllegalStateException("boom")).when(jdbcDc).execute(any(), any());

        when(queryConfig.getQueries("q")).thenReturn(List.of(JDBC_QUERY));
        when(datasourceConfig.getConnection(any(), any())).thenReturn(jdbcConnection);
        when(dcs.getConnection(jdbcConnection)).thenReturn(jdbcDc);

        assertThrows(IllegalStateException.class, () -> controller.getQuery("q"));
    }

    @Test
    void getQuery_wrapsCheckedExceptionFromExecuteInResponseStatusException() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        JdbcDatasourceConnection jdbcDc = spy(new JdbcDatasourceConnection(dataSource));
        doAnswer(invocation -> {
            throw new Exception("checked");
        }).when(jdbcDc).execute(any(), any());

        when(queryConfig.getQueries("q")).thenReturn(List.of(JDBC_QUERY));
        when(datasourceConfig.getConnection(any(), any())).thenReturn(jdbcConnection);
        when(dcs.getConnection(jdbcConnection)).thenReturn(jdbcDc);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.getQuery("q"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertEquals("Query execution failed", ex.getReason());
        assertInstanceOf(Exception.class, ex.getCause());
        assertEquals("checked", ex.getCause().getMessage());
    }
}
