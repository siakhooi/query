package sing.app.query.domain;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcDatasourceConnection implements DatasourceConnection {

    private JdbcTemplate jdbcTemplate;

    public JdbcDatasourceConnection(DataSource datasource) {
        this.jdbcTemplate = new JdbcTemplate(datasource);
    }

    @Override
    public List<Map<String, Object>> execute(String queryString, String collection, String filter, String fields, String sort) {
        log.debug("Executing JDBC query: {}", queryString);
        return jdbcTemplate.queryForList(queryString);
    }
}
