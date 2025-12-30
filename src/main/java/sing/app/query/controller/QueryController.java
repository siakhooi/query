package sing.app.query.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import sing.app.query.Util;
import sing.app.query.config.DatasourceConfig;
import sing.app.query.config.DatasourceConfig.Connection;
import sing.app.query.config.QueryConfig;
import sing.app.query.config.QueryConfig.Query;
import sing.app.query.domain.DatasourceConnection;
import sing.app.query.service.DatasourceConnectionService;

@RestController
@Slf4j
public class QueryController {
    private QueryConfig queryConfig;
    private DatasourceConfig datasourceConfig;
    private DatasourceConnectionService dcs;

    public QueryController(QueryConfig queryConfig, DatasourceConfig datasourceConfig,
            DatasourceConnectionService dcs) {
        this.queryConfig = queryConfig;
        this.datasourceConfig = datasourceConfig;
        this.dcs = dcs;
    }

    @GetMapping("/query/{querysetName}")
    @Operation(summary = Util.API_QUERY_SUMMARY, description = Util.API_QUERY_DESCRIPTION)
    @ApiResponse(responseCode = "200", description = "Success",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    public Map<String, List<Map<String, Object>>> getQuery(@PathVariable String querysetName) {
        log.info("Fetching query for queryset: {}", querysetName);

        List<Query> queries = queryConfig.getQueries(querysetName);

        if (queries.isEmpty()) {
            log.warn("No queries found for queryset: {}", querysetName);
            throw new ResponseStatusException(BAD_REQUEST, "No queries found");
        }
        Map<String, List<Map<String, Object>>> results = new HashMap<>();
        for (Query query : queries) {
            Connection connection;
            try {
                connection = datasourceConfig.getConnection(query.getConnection(), query.getName());

            } catch (Exception e) {
                log.error("Error fetching connection for query: {}", query.getName(), e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error fetching connection for query: " + query.getName());
            }
            DatasourceConnection dc = dcs.getConnection(connection);

            List<Map<String, Object>> result;
            if ("mongodb".equalsIgnoreCase(connection.getType())) {
                result = dc.execute(null, query.getMongoQuery());
            } else {
                result = dc.execute(query.getQueryString(), null);
            }
            results.put(query.getName(), result);
        }

        return results;
    }

}
