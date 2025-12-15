package sing.app.query.config;

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

    public List<Query> getQueries(String querysetName) {
        return getQuerysets().stream()
                .filter(qs -> qs.getName().equals(querysetName))
                .flatMap(qs -> qs.getQueries().stream())
                .toList();
    }

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

        @JsonIgnore
        private String queryString;

        @JsonIgnore
        private String collection;

        @JsonIgnore
        private String filter;

        @JsonIgnore
        private String fields;

        @NotBlank
        @JsonInclude(Include.NON_NULL)
        private String connection;
    }
}
