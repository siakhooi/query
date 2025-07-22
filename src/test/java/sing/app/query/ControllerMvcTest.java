package sing.app.query;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldReturnDefaultMessage() throws Exception {
		this.mockMvc
				.perform(get("/greeting"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello, World!")));
	}
	@Test
	void shouldReturnMessageWithName() throws Exception {
		this.mockMvc
				.perform(get("/greeting?name=Earth"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello, Earth!")));
	}
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
	@Test
	void shouldReturnConfiguDatasource() throws Exception {
		this.mockMvc
				.perform(get("/config/datasource"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(not(containsString("url"))))
				.andExpect(content().string(not(containsString("password"))))
				.andExpect(content().string(containsString("name")))
				.andExpect(content().string(containsString("username")))
				.andExpect(content().string(containsString("connections")));
	}

}
