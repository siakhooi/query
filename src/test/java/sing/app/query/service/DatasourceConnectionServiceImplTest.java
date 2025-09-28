package sing.app.query.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sing.app.query.config.DatasourceConfig.Connection;
import sing.app.query.domain.DatasourceConnection;
import sing.app.query.domain.MariadbDatasourceConnection;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatasourceConnectionServiceImplTest {

    private DatasourceConnectionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DatasourceConnectionServiceImpl();
    }

    @Test
    void testGetConnectionCreatesNewConnection() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("testdb");
        when(mockConn.getUrl()).thenReturn("jdbc:mariadb://localhost:3306/test");
        when(mockConn.getUsername()).thenReturn("user");
        when(mockConn.getPassword()).thenReturn("pass");

        DatasourceConnection result = service.getConnection(mockConn);

        assertNotNull(result);
        assertTrue(result instanceof MariadbDatasourceConnection);
    }

    @Test
    void testGetConnectionReusesExistingConnection() {
        Connection mockConn = mock(Connection.class);
        when(mockConn.getName()).thenReturn("testdb");
        when(mockConn.getUrl()).thenReturn("jdbc:mariadb://localhost:3306/test");
        when(mockConn.getUsername()).thenReturn("user");
        when(mockConn.getPassword()).thenReturn("pass");

        DatasourceConnection first = service.getConnection(mockConn);
        DatasourceConnection second = service.getConnection(mockConn);

        assertSame(first, second);
    }

    @Test
    void testGetConnectionWithDifferentNamesCreatesDifferentConnections() {
        Connection conn1 = mock(Connection.class);
        when(conn1.getName()).thenReturn("db1");
        when(conn1.getUrl()).thenReturn("jdbc:mariadb://localhost:3306/db1");
        when(conn1.getUsername()).thenReturn("user1");
        when(conn1.getPassword()).thenReturn("pass1");

        Connection conn2 = mock(Connection.class);
        when(conn2.getName()).thenReturn("db2");
        when(conn2.getUrl()).thenReturn("jdbc:mariadb://localhost:3306/db2");
        when(conn2.getUsername()).thenReturn("user2");
        when(conn2.getPassword()).thenReturn("pass2");

        DatasourceConnection result1 = service.getConnection(conn1);
        DatasourceConnection result2 = service.getConnection(conn2);

        assertNotSame(result1, result2);
    }
}