package sing.app.query.service;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;
import sing.app.query.config.DatasourceConfig.Connection;
import sing.app.query.domain.CassandraDatasourceConnection;
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
            } else if ("cassandra".equalsIgnoreCase(connection.getType())) {
                conn = createCassandraConnection(connection);
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

    private DatasourceConnection createCassandraConnection(Connection connection) {
        validateCassandraConfig(connection);
        CqlSession session = buildCassandraSession(connection);
        return new CassandraDatasourceConnection(session);
    }

    private void validateCassandraConfig(Connection connection) {
        if (connection.getUrl() == null || connection.getUrl().isBlank()) {
            throw new IllegalArgumentException("URL is required for Cassandra connection: " + connection.getName());
        }
        if (connection.getDatacenter() == null || connection.getDatacenter().isBlank()) {
            throw new IllegalArgumentException("Datacenter is required for Cassandra connection: " + connection.getName());
        }
    }

    protected CqlSession buildCassandraSession(Connection connection) {
        URI uri = parseCassandraUri(connection.getUrl());
        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Cassandra URL must include a host: " + connection.getUrl());
        }
        int port = uri.getPort() == -1 ? 9042 : uri.getPort();

        CqlSessionBuilder builder = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter(connection.getDatacenter());

        if (connection.getUsername() != null && !connection.getUsername().isBlank()) {
            builder = builder.withAuthCredentials(
                    connection.getUsername(),
                    connection.getPassword() != null ? connection.getPassword() : "");
        }

        if (connection.getKeyspace() != null && !connection.getKeyspace().isBlank()) {
            builder = builder.withKeyspace(connection.getKeyspace());
        }

        return builder.build();
    }

    private URI parseCassandraUri(String url) {
        try {
            URI uri = URI.create(url);
            if (uri.getScheme() == null) {
                uri = URI.create("cassandra://" + url);
            }
            return uri;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Cassandra URL: " + url, e);
        }
    }

}
