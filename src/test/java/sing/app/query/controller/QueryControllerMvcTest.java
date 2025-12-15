package sing.app.query.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import sing.app.query.config.DatasourceConfig;
import sing.app.query.domain.DatasourceConnection;
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
        DatasourceConnection dc=mock(DatasourceConnection.class);
        when(dcs.getConnection(any())).thenReturn(dc);
        List<Map<String, Object>> result=new ArrayList<>();
        when(dc.execute(any(), any(), any(), any())).thenReturn(result);
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
        when(datasourceConfig.getConnections(any())).thenReturn(new ArrayList<>());
        this.mockMvc
                .perform(get("/query/fruits"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    void tooManyConnectionsFound() throws Exception {
        when(datasourceConfig.getConnections(any())).thenReturn(List.of(new DatasourceConfig.Connection(), new DatasourceConfig.Connection()));
        this.mockMvc
                .perform(get("/query/fruits"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
