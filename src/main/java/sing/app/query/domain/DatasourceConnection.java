package sing.app.query.domain;

import java.util.List;
import java.util.Map;
import sing.app.query.config.MongoQuery;

public sealed interface DatasourceConnection permits JdbcDatasourceConnection, MongodbDatasourceConnection,
        CassandraDatasourceConnection {

    List<Map<String, Object>> execute(String queryString, MongoQuery mongoQuery);

}
