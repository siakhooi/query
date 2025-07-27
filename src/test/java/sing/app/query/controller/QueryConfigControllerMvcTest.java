package sing.app.query.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QueryConfigControllerMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnConfiguQuery() throws Exception {
        this.mockMvc
                .perform(get("/config/query"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name")))
                .andExpect(content().string(containsString("queries")))
                .andExpect(content().string(not(containsString("queryString"))))
                .andExpect(content().string(containsString("connection")));
    }

}
