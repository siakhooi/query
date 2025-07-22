package sing.app.query;

import java.util.ArrayList;
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
@ConfigurationProperties(prefix = "query")
public class QueryConfig {

    @NotNull
    @Size(min = 1)
    private List<Queryset> querysets = new ArrayList<>();

    @Data
    public static class Queryset {
        @NotBlank
        private String name;

        @NotBlank
        private List<Query> queries = new ArrayList<>();

    }

    @Data
    public static class Query {
        @NotBlank
        private String name;

        @NotBlank
        @JsonIgnore
        private String queryString;

        @NotBlank
        @JsonInclude(Include.NON_NULL)
        private String connection;
    }
}
