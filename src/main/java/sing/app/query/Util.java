package sing.app.query;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

    public static final String API_QUERY_SUMMARY = "Return query configuration";
    public static final String API_QUERY_DESCRIPTION = """
            return query configuration in query.yaml
            """;

    public static final String API_DATASOURCE_SUMMARY = "Return datasource configuration";
    public static final String API_DATASOURCE_DESCRIPTION = """
            return datasource configuration in datasource.yaml
            """;

}
