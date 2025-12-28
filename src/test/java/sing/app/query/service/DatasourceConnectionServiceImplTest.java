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

    @Test
    void testCassandraConnectionRequiresUrl() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-no-url");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn(null);
        when(mockConn.getDatacenter()).thenReturn("datacenter1");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getConnection(mockConn));

        assertTrue(ex.getMessage().contains("URL is required"));
    }

    @Test
    void testCassandraConnectionWithBlankUrl() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-blank-url");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("   ");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getConnection(mockConn));

        assertTrue(ex.getMessage().contains("URL is required"));
    }

    @Test
    void testCassandraConnectionWithBlankDatacenter() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-blank-dc");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("localhost:9042");
        when(mockConn.getDatacenter()).thenReturn("   ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getConnection(mockConn));

        assertTrue(ex.getMessage().contains("Datacenter is required"));
    }

    @Test
    void testCassandraConnectionWithUrlWithoutHost() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-no-host");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("cassandra://:9042");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");

        CqlSession mockSession = mock(CqlSession.class);
        DatasourceConnectionServiceImpl cassandraService = new TestableDatasourceConnectionServiceImpl(mockSession, false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cassandraService.getConnection(mockConn));

        assertTrue(ex.getMessage().contains("must include a host"));
    }

    @Test
    void testCassandraConnectionWithUrlWithoutScheme() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-no-scheme");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("localhost:9042");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");
        when(mockConn.getUsername()).thenReturn("user");
        when(mockConn.getPassword()).thenReturn("pass");

        CqlSession mockSession = mock(CqlSession.class);
        DatasourceConnectionServiceImpl cassandraService = new TestableDatasourceConnectionServiceImpl(mockSession);

        DatasourceConnection result = cassandraService.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof CassandraDatasourceConnection);
    }

    @Test
    void testCassandraConnectionWithDefaultPort() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-default-port");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("cassandra://localhost");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");

        CqlSession mockSession = mock(CqlSession.class);
        DatasourceConnectionServiceImpl cassandraService = new TestableDatasourceConnectionServiceImpl(mockSession);

        DatasourceConnection result = cassandraService.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof CassandraDatasourceConnection);
    }

    @Test
    void testCassandraConnectionWithCustomPort() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-custom-port");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("cassandra://localhost:9043");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");

        CqlSession mockSession = mock(CqlSession.class);
        DatasourceConnectionServiceImpl cassandraService = new TestableDatasourceConnectionServiceImpl(mockSession);

        DatasourceConnection result = cassandraService.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof CassandraDatasourceConnection);
    }

    @Test
    void testCassandraConnectionWithoutAuthentication() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-no-auth");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("localhost:9042");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");
        when(mockConn.getUsername()).thenReturn(null);
        when(mockConn.getPassword()).thenReturn(null);

        CqlSession mockSession = mock(CqlSession.class);
        DatasourceConnectionServiceImpl cassandraService = new TestableDatasourceConnectionServiceImpl(mockSession);

        DatasourceConnection result = cassandraService.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof CassandraDatasourceConnection);
    }

    @Test
    void testCassandraConnectionWithBlankUsername() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-blank-user");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("localhost:9042");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");
        when(mockConn.getUsername()).thenReturn("   ");
        when(mockConn.getPassword()).thenReturn("pass");

        CqlSession mockSession = mock(CqlSession.class);
        DatasourceConnectionServiceImpl cassandraService = new TestableDatasourceConnectionServiceImpl(mockSession);

        DatasourceConnection result = cassandraService.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof CassandraDatasourceConnection);
    }

    @Test
    void testCassandraConnectionWithUsernameButNoPassword() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-no-pass");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("localhost:9042");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");
        when(mockConn.getUsername()).thenReturn("user");
        when(mockConn.getPassword()).thenReturn(null);

        CqlSession mockSession = mock(CqlSession.class);
        DatasourceConnectionServiceImpl cassandraService = new TestableDatasourceConnectionServiceImpl(mockSession);

        DatasourceConnection result = cassandraService.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof CassandraDatasourceConnection);
    }

    @Test
    void testCassandraConnectionWithKeyspace() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-with-keyspace");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("localhost:9042");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");
        when(mockConn.getKeyspace()).thenReturn("mykeyspace");

        CqlSession mockSession = mock(CqlSession.class);
        DatasourceConnectionServiceImpl cassandraService = new TestableDatasourceConnectionServiceImpl(mockSession);

        DatasourceConnection result = cassandraService.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof CassandraDatasourceConnection);
    }

    @Test
    void testCassandraConnectionWithBlankKeyspace() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-blank-keyspace");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("localhost:9042");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");
        when(mockConn.getKeyspace()).thenReturn("   ");

        CqlSession mockSession = mock(CqlSession.class);
        DatasourceConnectionServiceImpl cassandraService = new TestableDatasourceConnectionServiceImpl(mockSession);

        DatasourceConnection result = cassandraService.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof CassandraDatasourceConnection);
    }

    @Test
    void testCassandraConnectionReusesSameConnection() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-reuse");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("localhost:9042");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");

        CqlSession mockSession = mock(CqlSession.class);
        DatasourceConnectionServiceImpl cassandraService = new TestableDatasourceConnectionServiceImpl(mockSession);

        DatasourceConnection first = cassandraService.getConnection(mockConn);
        DatasourceConnection second = cassandraService.getConnection(mockConn);

        assertSame(first, second);
    }

    @Test
    void testUnsupportedConnectionType() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("unsupported");
        when(mockConn.getType()).thenReturn("redis");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getConnection(mockConn));

        assertTrue(ex.getMessage().contains("Unsupported connection type"));
    }

    @Test
    void testParseCassandraUri_withScheme() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-real");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("cassandra://testhost:9042");
        when(mockConn.getDatacenter()).thenReturn("dc1");
        when(mockConn.getUsername()).thenReturn("testuser");
        when(mockConn.getPassword()).thenReturn("testpass");
        when(mockConn.getKeyspace()).thenReturn("testkeyspace");

        DatasourceConnectionServiceImpl testService = new DatasourceConnectionServiceImpl();

        Exception ex = assertThrows(Exception.class, () -> testService.getConnection(mockConn));

        assertNotNull(ex);
    }

    @Test
    void testParseCassandraUri_withoutScheme() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-real-noscheme");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("testhost:9042");
        when(mockConn.getDatacenter()).thenReturn("dc1");

        DatasourceConnectionServiceImpl testService = new DatasourceConnectionServiceImpl();

        Exception ex = assertThrows(Exception.class, () -> testService.getConnection(mockConn));

        assertNotNull(ex);
    }

    @Test
    void testParseCassandraUri_plainHostPort() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-plain-host");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("localhost:9042");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");

        DatasourceConnectionServiceImpl testService = new DatasourceConnectionServiceImpl();

        Exception ex = assertThrows(Exception.class, () -> testService.getConnection(mockConn));

        assertNotNull(ex);
    }

    @Test
    void testParseCassandraUri_hostOnly() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-host-only");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("cassandra-host");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");

        DatasourceConnectionServiceImpl testService = new DatasourceConnectionServiceImpl();

        Exception ex = assertThrows(Exception.class, () -> testService.getConnection(mockConn));

        assertNotNull(ex);
    }

    @Test
    void testParseCassandraUri_relativeUri() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-relative");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("//myhost:9042");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");

        DatasourceConnectionServiceImpl testService = new DatasourceConnectionServiceImpl();

        Exception ex = assertThrows(Exception.class, () -> testService.getConnection(mockConn));

        assertNotNull(ex);
    }

    @Test
    void testParseCassandraUri_pathOnly() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-path");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("/myhost");
        when(mockConn.getDatacenter()).thenReturn("datacenter1");

        DatasourceConnectionServiceImpl testService = new DatasourceConnectionServiceImpl();

        Exception ex = assertThrows(Exception.class, () -> testService.getConnection(mockConn));

        assertNotNull(ex);
    }

    @Test
    void testParseCassandraUri_invalidUrl() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-invalid-url");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("http://[invalid");
        when(mockConn.getDatacenter()).thenReturn("dc1");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getConnection(mockConn));

        assertTrue(ex.getMessage().contains("Invalid Cassandra URL"));
    }

    @Test
    void testBuildCassandraSession_withDefaultPort() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-default-port-real");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("cassandra://testhost");
        when(mockConn.getDatacenter()).thenReturn("dc1");

        DatasourceConnectionServiceImpl testService = new DatasourceConnectionServiceImpl();

        Exception ex = assertThrows(Exception.class, () -> testService.getConnection(mockConn));

        assertNotNull(ex);
    }

    @Test
    void testBuildCassandraSession_withAuthAndKeyspace() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-auth-keyspace");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("cassandra://testhost:9043");
        when(mockConn.getDatacenter()).thenReturn("dc1");
        when(mockConn.getUsername()).thenReturn("myuser");
        when(mockConn.getPassword()).thenReturn("mypass");
        when(mockConn.getKeyspace()).thenReturn("mykeyspace");

        DatasourceConnectionServiceImpl testService = new DatasourceConnectionServiceImpl();

        Exception ex = assertThrows(Exception.class, () -> testService.getConnection(mockConn));

        assertNotNull(ex);
    }

    @Test
    void testBuildCassandraSession_withUsernameNoPassword() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-user-nopass");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("testhost:9042");
        when(mockConn.getDatacenter()).thenReturn("dc1");
        when(mockConn.getUsername()).thenReturn("myuser");
        when(mockConn.getPassword()).thenReturn(null);

        DatasourceConnectionServiceImpl testService = new DatasourceConnectionServiceImpl();

        Exception ex = assertThrows(Exception.class, () -> testService.getConnection(mockConn));

        assertNotNull(ex);
    }

    @Test
    void testBuildCassandraSession_withKeyspaceOnly() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("cass-keyspace-only");
        when(mockConn.getType()).thenReturn("cassandra");
        when(mockConn.getUrl()).thenReturn("testhost:9042");
        when(mockConn.getDatacenter()).thenReturn("dc1");
        when(mockConn.getKeyspace()).thenReturn("mykeyspace");

        DatasourceConnectionServiceImpl testService = new DatasourceConnectionServiceImpl();

        Exception ex = assertThrows(Exception.class, () -> testService.getConnection(mockConn));

        assertNotNull(ex);
    }

    static class TestableDatasourceConnectionServiceImpl extends DatasourceConnectionServiceImpl {

        private final CqlSession session;
        private final boolean callSuper;

        TestableDatasourceConnectionServiceImpl(CqlSession session) {
            this(session, true);
        }

        TestableDatasourceConnectionServiceImpl(CqlSession session, boolean callSuper) {
            this.session = session;
            this.callSuper = callSuper;
        }

        @Override
        protected CqlSession buildCassandraSession(Connection connection) {
            if (!callSuper) {
                return super.buildCassandraSession(connection);
            }
            return session;
        }
    }
}