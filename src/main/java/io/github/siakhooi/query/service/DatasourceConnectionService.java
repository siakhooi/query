package io.github.siakhooi.query.service;

import io.github.siakhooi.query.config.DatasourceConfig.Connection;
import io.github.siakhooi.query.domain.DatasourceConnection;

public interface DatasourceConnectionService {

    public DatasourceConnection getConnection(Connection connection);

}
