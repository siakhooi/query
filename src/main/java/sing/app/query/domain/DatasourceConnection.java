package sing.app.query.domain;

import java.util.List;
import java.util.Map;
import sing.app.query.config.MongoQuery;

public interface DatasourceConnection {

    List<Map<String, Object>> execute(String queryString, MongoQuery mongoQuery);

}
