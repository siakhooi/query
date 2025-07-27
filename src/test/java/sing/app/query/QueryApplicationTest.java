package sing.app.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import sing.app.query.controller.DatasourceConfigController;
import sing.app.query.controller.QueryConfigController;

@SpringBootTest
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

    @Test
    void mainMethodRunsWithoutException() {
        // Arrange
        String[] args = {};

        // Act & Assert
        assertDoesNotThrow(() -> {
            QueryApplication.main(args);
        });
    }
}
