package io.github.siakhooi.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.siakhooi.query.controller.DatasourceConfigController;
import io.github.siakhooi.query.controller.QueryConfigController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QueryApplicationTest {

    @Autowired
    private QueryConfigController queryConfigController;

    @Autowired
    private DatasourceConfigController datasourceConfigController;

    @Test
    void contextLoads() {
        assertThat(queryConfigController).isNotNull();
        assertThat(datasourceConfigController).isNotNull();
    }
}
