package io.github.siakhooi.query.domain;

import java.util.List;
import java.util.Map;
import io.github.siakhooi.query.config.MongoQuery;

public sealed interface DatasourceConnection permits JdbcDatasourceConnection, MongodbDatasourceConnection,
        CassandraDatasourceConnection {

    List<Map<String, Object>> execute(String queryString, MongoQuery mongoQuery);

    /** Close underlying client/pool; required when config is reloaded (e.g. K8s Secret change). */
    void close();
}
