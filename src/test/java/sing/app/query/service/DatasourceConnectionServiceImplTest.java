package sing.app.query.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.datastax.oss.driver.api.core.CqlSession;

import sing.app.query.config.DatasourceConfig.Connection;
import sing.app.query.domain.CassandraDatasourceConnection;
import sing.app.query.domain.DatasourceConnection;
import sing.app.query.domain.JdbcDatasourceConnection;
import sing.app.query.domain.MongodbDatasourceConnection;

class DatasourceConnectionServiceImplTest {

    private DatasourceConnectionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DatasourceConnectionServiceImpl();
    }

    @Test
    void testGetConnectionCreatesNewJdbcConnection() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("testdb");
        when(mockConn.getType()).thenReturn("jdbc");
        when(mockConn.getUrl()).thenReturn("jdbc:mariadb://localhost:3306/test");
        when(mockConn.getUsername()).thenReturn("user");
        when(mockConn.getPassword()).thenReturn("pass");
        when(mockConn.getMaximumPoolSize()).thenReturn(10);
        when(mockConn.getMinimumIdle()).thenReturn(2);

        DatasourceConnection result = service.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof JdbcDatasourceConnection);
    }

    @Test
    void testGetConnectionCreatesNewMongodbConnection() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("mongodb-test");
        when(mockConn.getType()).thenReturn("mongodb");
        when(mockConn.getUrl()).thenReturn("mongodb://localhost:27017");
        when(mockConn.getDatabase()).thenReturn("testdb");
        when(mockConn.getUsername()).thenReturn("user");
        when(mockConn.getPassword()).thenReturn("pass");

        DatasourceConnection result = service.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof MongodbDatasourceConnection);
    }

    @Test
    void testGetConnectionReusesExistingConnection() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("testdb");
        when(mockConn.getType()).thenReturn("jdbc");
        when(mockConn.getUrl()).thenReturn("jdbc:mariadb://localhost:3306/test");
        when(mockConn.getUsername()).thenReturn("user");
        when(mockConn.getPassword()).thenReturn("pass");
        when(mockConn.getMaximumPoolSize()).thenReturn(10);
        when(mockConn.getMinimumIdle()).thenReturn(2);

        DatasourceConnection first = service.getConnection(mockConn);
        DatasourceConnection second = service.getConnection(mockConn);

        assertSame(first, second);
    }

    @Test
    void testGetConnectionWithDifferentNamesCreatesDifferentConnections() {
        Connection conn1 = mock(Connection.class);
        when(conn1.getName()).thenReturn("db1");
        when(conn1.getType()).thenReturn("jdbc");
        when(conn1.getUrl()).thenReturn("jdbc:mariadb://localhost:3306/db1");
        when(conn1.getUsername()).thenReturn("user1");
        when(conn1.getPassword()).thenReturn("pass1");
        when(conn1.getMaximumPoolSize()).thenReturn(10);
        when(conn1.getMinimumIdle()).thenReturn(2);

        Connection conn2 = mock(Connection.class);
        when(conn2.getName()).thenReturn("db2");
        when(conn2.getType()).thenReturn("jdbc");
        when(conn2.getUrl()).thenReturn("jdbc:mariadb://localhost:3306/db2");
        when(conn2.getUsername()).thenReturn("user2");
        when(conn2.getPassword()).thenReturn("pass2");
        when(conn2.getMaximumPoolSize()).thenReturn(10);
        when(conn2.getMinimumIdle()).thenReturn(2);

        DatasourceConnection result1 = service.getConnection(conn1);
        DatasourceConnection result2 = service.getConnection(conn2);

        assertNotSame(result1, result2);
    }

    @Test
    void testGetConnectionCreatesNewCassandraConnection() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-test");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("cassandra://localhost:9042");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");
        when(mockConn.getKeyspace()).thenReturn("ks1");
        when(mockConn.getUsername()).thenReturn("user");
        when(mockConn.getPassword()).thenReturn("pass");

        CqlSession mockSession = mock(CqlSession.class);
        DatasourceConnectionServiceImpl cassandraService = new TestableDatasourceConnectionServiceImpl(mockSession);

        DatasourceConnection result = cassandraService.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof CassandraDatasourceConnection);
    }

    @Test
    void testCassandraConnectionRequiresDatacenter() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-invalid");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("cassandra://localhost:9042");
        when(mockConn.getDatacenter()).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getConnection(mockConn));

        assertTrue(ex.getMessage().contains("Datacenter"));
    }

    static class TestableDatasourceConnectionServiceImpl extends DatasourceConnectionServiceImpl {

        private final CqlSession session;

        TestableDatasourceConnectionServiceImpl(CqlSession session) {
            this.session = session;
        }

        @Override
        protected CqlSession buildCassandraSession(Connection connection) {
            return session;
        }
    }
}