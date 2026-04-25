package sing.app.query.domain;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import sing.app.query.config.MongoQuery;

@Slf4j
public final class JdbcDatasourceConnection implements DatasourceConnection {

    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public JdbcDatasourceConnection(DataSource datasource) {
        this.dataSource = datasource;
        this.jdbcTemplate = new JdbcTemplate(datasource);
    }

    @Override
    public void close() {
        if (dataSource instanceof HikariDataSource h) {
            h.close();
        }
    }

    @Override
    public List<Map<String, Object>> execute(String queryString, MongoQuery mongoQuery) {
        log.debug("Executing JDBC query: {}", queryString);
        return jdbcTemplate.queryForList(queryString);
    }
}
