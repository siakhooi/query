package sing.app.query.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatasourceConfigTest {
    private DatasourceConfig config;
    private DatasourceConfig.Connection conn1;
    private DatasourceConfig.Connection conn2;

    @BeforeEach
    void setUp() {
        config = new DatasourceConfig();
        conn1 = new DatasourceConfig.Connection();
        conn1.setName("test1");
        conn1.setType("mysql");
        conn1.setUrl("jdbc:mysql://localhost/test1");
        conn2 = new DatasourceConfig.Connection();
        conn2.setName("test2");
        conn2.setType("postgres");
        conn2.setUrl("jdbc:postgresql://localhost/test2");
        config.setConnections(List.of(conn1, conn2));
    }

    @Test
    void getConnection_returnsCorrectConnection() throws DatasourceConfigException {
        DatasourceConfig.Connection result = config.getConnection("test1", "query1");
        assertEquals("test1", result.getName());
        assertEquals("mysql", result.getType());
    }

    @Test
    void getConnection_throwsWhenNoConnectionFound() {
        DatasourceConfigException ex = assertThrows(DatasourceConfigException.class, () ->
            config.getConnection("notfound", "query2")
        );
        assertTrue(ex.getMessage().contains("No connections found"));
    }

    @Test
    void getConnection_throwsWhenMultipleConnectionsFound() {
        DatasourceConfig.Connection duplicate = new DatasourceConfig.Connection();
        duplicate.setName("test1");
        duplicate.setType("mysql");
        duplicate.setUrl("jdbc:mysql://localhost/test1");
        config.setConnections(List.of(conn1, duplicate));
        DatasourceConfigException ex = assertThrows(DatasourceConfigException.class, () ->
            config.getConnection("test1", "query3")
        );
        assertTrue(ex.getMessage().contains("Multiple connections found"));
    }
}
