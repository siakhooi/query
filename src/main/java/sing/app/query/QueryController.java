package sing.app.query;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class QueryController {

    private GreetingConfig config;
    private QueryConfig queryConfig;
    private DatasourceConfig datasourceConfig;

    public QueryController(GreetingConfig config, QueryConfig queryConfig, DatasourceConfig datasourceConfig) {
        this.config = config;
        this.queryConfig = queryConfig;
        this.datasourceConfig = datasourceConfig;
    }

    private static final String GREETING_TEMPLATE = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    @Operation(summary = Util.API_GREETING_SUMMARY, description = Util.API_GREETING_DESCRIPTION)
    @ApiResponse(responseCode = "200", description = "Success", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) })
    @Parameter(name = "name", required = false, description = "the hello goes to this name")
    public Greeting greeting(@RequestParam(value = "name", required = false) String name) {

        String displayName = name == null ? config.getDefaultMessage() : name;

        log.info("greeting: name: {}", displayName);

        return new Greeting(counter.incrementAndGet(), String.format(GREETING_TEMPLATE, displayName));
    }

    @GetMapping("/config/query")
    @Operation(summary = Util.API_QUERY_SUMMARY, description = Util.API_QUERY_DESCRIPTION)
    @ApiResponse(responseCode = "200", description = "Success", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) })
    public QueryConfig getQueryConfig() {
        QueryConfig qc = new QueryConfig();
        for (var qs : queryConfig.getQuerysets()) {
            QueryConfig.Queryset qset = new QueryConfig.Queryset();
            qset.setName(qs.getName());
            for (var q : qs.getQueries()) {
                QueryConfig.Query query = new QueryConfig.Query();
                query.setName(q.getName());
                query.setConnection(q.getConnection());
                qset.getQueries().add(query);
            }
            qc.getQuerysets().add(qset);
        }

        return qc;
    }

    @GetMapping("/config/datasource")
    @Operation(summary = Util.API_DATASOURCE_SUMMARY, description = Util.API_DATASOURCE_DESCRIPTION)
    @ApiResponse(responseCode = "200", description = "Success", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) })
    public DatasourceConfig getDatasourceConfig() {
        DatasourceConfig dc = new DatasourceConfig();
        for (var c : datasourceConfig.getConnections()) {
            DatasourceConfig.Connection connection = new DatasourceConfig.Connection();
            connection.setName(c.getName());
            connection.setUsername(c.getUsername());
            dc.getConnections().add(connection);
        }
        return dc;
    }

}
