package sing.app.query.domain;

import java.util.List;
import java.util.Map;

public interface DatasourceConnection {

    List<Map<String, Object>> execute(String queryString, String collection, String filter);

}
