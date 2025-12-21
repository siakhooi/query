package sing.app.query.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class MongoQuery {

    @JsonIgnore
    private String collection;

    @JsonIgnore
    private String filter;

    @JsonIgnore
    private String fields;

    @JsonIgnore
    private String sort;

    @JsonIgnore
    private String pipeline;
}
