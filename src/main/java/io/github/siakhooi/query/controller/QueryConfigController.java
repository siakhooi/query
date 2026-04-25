package io.github.siakhooi.query.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import io.github.siakhooi.query.Util;
import io.github.siakhooi.query.config.QueryConfig;

@RestController
@Slf4j
public class QueryConfigController {
    private QueryConfig queryConfig;

    public QueryConfigController(QueryConfig queryConfig) {
        this.queryConfig = queryConfig;
    }

    @GetMapping("/config/query")
    @Operation(summary = Util.API_CONFIG_QUERY_SUMMARY, description = Util.API_CONFIG_QUERY_DESCRIPTION)
    @ApiResponse(responseCode = "200", description = "Success", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) })
    public QueryConfig getQueryConfig() {
        QueryConfig qc = new QueryConfig();
        for (var qs : queryConfig.getQuerysets()) {
            List<QueryConfig.Query> stripped = new ArrayList<>();
            for (var q : qs.queries()) {
                stripped.add(new QueryConfig.Query(q.name(), null, null, q.connection()));
            }
            qc.getQuerysets().add(new QueryConfig.Queryset(qs.name(), stripped));
        }

        return qc;
    }

}
