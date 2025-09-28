package sing.app.query.service;

import sing.app.query.config.DatasourceConfig.Connection;
import sing.app.query.domain.DatasourceConnection;

public interface DatasourceConnectionService {

    public DatasourceConnection getConnection(Connection connection);

}
