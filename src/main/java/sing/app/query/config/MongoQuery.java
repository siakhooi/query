package sing.app.query.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record MongoQuery(
        @JsonIgnore String collection,
        @JsonIgnore String filter,
        @JsonIgnore String fields,
        @JsonIgnore String sort,
        @JsonIgnore String pipeline) {
}
