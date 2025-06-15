package sing.app.query;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Configuration
@Data
@Validated
@ConfigurationProperties(prefix = "datasource")
public class DatasourceConfig {

    @NotNull
    @Size(min = 1)
    private List<Connection> connections = new java.util.ArrayList<>();

    @Data
    public static class Connection {
        @NotBlank
        private String name;

        @NotBlank
        private String url;

        private String username;

        private String password;

    }
}
