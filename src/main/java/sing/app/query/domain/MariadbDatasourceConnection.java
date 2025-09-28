package sing.app.query.domain;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MariadbDatasourceConnection implements DatasourceConnection {

    private JdbcTemplate jdbcTemplate;

    public MariadbDatasourceConnection(DataSource datasource) {
        this.jdbcTemplate = new JdbcTemplate(datasource);
    }

    @Override
    public List<Map<String, Object>> execute(String queryString) {
        log.debug("Executing {}", queryString);
        return jdbcTemplate.queryForList(queryString);

    }

}
