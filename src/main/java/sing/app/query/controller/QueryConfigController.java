package sing.app.query.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import sing.app.query.Util;
import sing.app.query.config.QueryConfig;

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

}
