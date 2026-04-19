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
                .filter(qs -> qs.name().equals(querysetName))
                .flatMap(qs -> qs.queries().stream())
                .toList();
    }

    public static record Queryset(
            @NotBlank String name,
            List<Query> queries) {
        public Queryset {
            queries = queries == null ? new ArrayList<>() : new ArrayList<>(queries);
        }
    }

    public static record Query(
            @NotBlank String name,
            @JsonIgnore String queryString,
            MongoQuery mongoQuery,
            @NotBlank @JsonInclude(Include.NON_NULL) String connection) {
    }
}
