package sing.app.query.config;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

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
        @JsonInclude(Include.NON_NULL)
        private String name;

        @NotBlank
        @JsonIgnore
        private String url;

        @JsonInclude(Include.NON_NULL)
        private String username;

        @JsonIgnore
        private String password;

    }

    public List<Connection> getConnections(String connection) {
        return getConnections().stream()
                .filter(conn -> conn.getName().equals(connection))
                .toList();
    }
}
