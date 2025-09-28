package sing.app.query;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

    public static final String API_CONFIG_QUERY_SUMMARY = "Return query configuration";
    public static final String API_CONFIG_QUERY_DESCRIPTION = """
            return query configuration in query.yaml
            """;

    public static final String API_CONFIG_DATASOURCE_SUMMARY = "Return datasource configuration";
    public static final String API_CONFIG_DATASOURCE_DESCRIPTION = """
            return datasource configuration in datasource.yaml
            """;

    public static final String API_QUERY_SUMMARY = "Return query execution result";
    public static final String API_QUERY_DESCRIPTION = """
            return query execution result
            """;

}
