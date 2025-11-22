package sing.app.query.service;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(connection.getUrl());
            config.setUsername(connection.getUsername());
            config.setPassword(connection.getPassword());

            // Set pool name for better monitoring
            config.setPoolName(connection.getName() + "-pool");

            // Configure pool settings (with defaults)
            config.setMaximumPoolSize(connection.getMaximumPoolSize() != null ? connection.getMaximumPoolSize() : 10);
            config.setMinimumIdle(connection.getMinimumIdle() != null ? connection.getMinimumIdle() : 2);
            config.setConnectionTimeout(connection.getConnectionTimeout() != null ? connection.getConnectionTimeout() : 30000);
            config.setIdleTimeout(connection.getIdleTimeout() != null ? connection.getIdleTimeout() : 600000);
            config.setMaxLifetime(connection.getMaxLifetime() != null ? connection.getMaxLifetime() : 1800000);

            // Disable initialization fail fast to allow lazy initialization
            config.setInitializationFailTimeout(-1);

            DataSource ds = new HikariDataSource(config);
            DatasourceConnection conn = new MariadbDatasourceConnection(ds);
            connections.put(connection.getName(), conn);
            log.debug("Created new connection: {}", connection.getName());

            return conn;
        }
    }

}
