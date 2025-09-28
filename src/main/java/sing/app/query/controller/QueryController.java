package sing.app.query.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
public class QueryController {
    private QueryConfig queryConfig;
    private DatasourceConfig datasourceConfig;
    private DatasourceConnectionService dcs;

    public QueryController(QueryConfig queryConfig, DatasourceConfig datasourceConfig, DatasourceConnectionService dcs) {
        this.queryConfig = queryConfig;
        this.datasourceConfig = datasourceConfig;
        this.dcs = dcs;
    }

    @GetMapping("/query/{querysetName}")
    @Operation(summary = Util.API_QUERY_SUMMARY, description = Util.API_QUERY_DESCRIPTION)
    @ApiResponse(responseCode = "200", description = "Success", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) })
    public Map<String,List<Map<String, Object>>> getQuery(@PathVariable String querysetName) {
        log.info("Fetching query for queryset: {}", querysetName);

        List<Query> queries = queryConfig.getQueries(querysetName);

        if (queries.isEmpty()) {
            log.warn("No queries found for queryset: {}", querysetName);
            throw new ResponseStatusException(BAD_REQUEST, "No queries found");
        }
        Map<String, List<Map<String, Object>>> results = new HashMap<>();
        for (Query query : queries) {
            List<Connection> connections = datasourceConfig.getConnections(query.getConnection());
            if (connections.isEmpty()) {
                log.warn("No connections found for query: {}", query.getName());
                throw new ResponseStatusException(BAD_REQUEST, "No connections found");
            }
            if (connections.size() > 1) {
                log.warn("Multiple connections found for query: {}", query.getName());
                throw new ResponseStatusException(BAD_REQUEST, "Multiple connections found");
            }
            DatasourceConnection dc = dcs.getConnection(connections.get(0));
            List<Map<String, Object>> result = dc.execute(query.getQueryString());
            results.put(query.getName(), result);
        }

        return results;
    }

}
