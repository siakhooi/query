package sing.app.query.service;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sing.app.query.config.DatasourceConfig.Connection;
import sing.app.query.domain.DatasourceConnection;
import sing.app.query.domain.MariadbDatasourceConnection;

@Service
@Slf4j
public class DatasourceConnectionServiceImpl implements DatasourceConnectionService {
    private Map<String, DatasourceConnection> connections = new HashMap<>();

    @Override
    public DatasourceConnection getConnection(Connection connection) {

        if (connections.containsKey(connection.getName())) {
            log.debug("Reusing connection: {}", connection.getName());
            return connections.get(connection.getName());

        } else {
            DataSource ds = new DriverManagerDataSource(connection.getUrl(), connection.getUsername(),
                    connection.getPassword());
            DatasourceConnection conn = new MariadbDatasourceConnection(ds);
            connections.put(connection.getName(), conn);
            log.debug("Created new connection: {}", connection.getName());

            return conn;
        }
    }

}
