package sing.app.query.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import sing.app.query.config.DatasourceConfig;
import sing.app.query.domain.JdbcDatasourceConnection;
import sing.app.query.service.DatasourceConnectionService;

@SpringBootTest
@AutoConfigureMockMvc
class QueryControllerMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DatasourceConnectionService dcs;
    @MockitoSpyBean
    private DatasourceConfig datasourceConfig;


    @BeforeEach
    void setUp() {
        DataSource dataSource = mock(DataSource.class);
        JdbcDatasourceConnection jdbc = spy(new JdbcDatasourceConnection(dataSource));
        doReturn(new ArrayList<Map<String, Object>>()).when(jdbc).execute(any(), any());
        when(dcs.getConnection(any())).thenReturn(jdbc);
    }

    @Test
    void shouldReturnQuery() throws Exception {
        this.mockMvc
                .perform(get("/query/fruits"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("fruits")));
    }
    @Test
    void noQuerysetFound() throws Exception {
        this.mockMvc
                .perform(get("/query/fruits1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    void noConnectionsFound() throws Exception {
        when(datasourceConfig.getConnections()).thenReturn(new ArrayList<>());
        this.mockMvc
            .perform(get("/query/fruits"))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }
    @Test
    void tooManyConnectionsFound() throws Exception {
        when(datasourceConfig.getConnections()).thenReturn(List.of(new DatasourceConfig.Connection(), new DatasourceConfig.Connection()));
        this.mockMvc
                .perform(get("/query/fruits"))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

}
