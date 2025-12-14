package sing.app.query.service;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;
import sing.app.query.config.DatasourceConfig.Connection;
import sing.app.query.domain.DatasourceConnection;
import sing.app.query.domain.JdbcDatasourceConnection;
import sing.app.query.domain.MongodbDatasourceConnection;

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
            DatasourceConnection conn;

            if ("jdbc".equalsIgnoreCase(connection.getType())) {
                conn = createJdbcConnection(connection);
            } else if ("mongodb".equalsIgnoreCase(connection.getType())) {
                conn = createMongodbConnection(connection);
            } else {
                throw new IllegalArgumentException("Unsupported connection type: " + connection.getType());
            }

            connections.put(connection.getName(), conn);
            log.debug("Created new {} connection: {}", connection.getType(), connection.getName());

            return conn;
        }
    }

    private DatasourceConnection createJdbcConnection(Connection connection) {
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
        return new JdbcDatasourceConnection(ds);
    }

    private DatasourceConnection createMongodbConnection(Connection connection) {
        MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connection.getUrl()));

        // Add authentication if username and password are provided
        if (connection.getUsername() != null && !connection.getUsername().isEmpty()) {
            MongoCredential credential = MongoCredential.createCredential(
                connection.getUsername(),
                connection.getDatabase() != null ? connection.getDatabase() : "admin",
                connection.getPassword() != null ? connection.getPassword().toCharArray() : new char[0]
            );
            settingsBuilder.credential(credential);
        }

        MongoClient mongoClient = MongoClients.create(settingsBuilder.build());

        String databaseName = connection.getDatabase();
        if (databaseName == null || databaseName.isEmpty()) {
            throw new IllegalArgumentException("Database name is required for MongoDB connection: " + connection.getName());
        }

        return new MongodbDatasourceConnection(mongoClient, databaseName);
    }

}
