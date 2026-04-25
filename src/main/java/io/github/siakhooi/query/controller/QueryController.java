package io.github.siakhooi.query.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import io.github.siakhooi.query.Util;
import io.github.siakhooi.query.config.DatasourceConfig;
import io.github.siakhooi.query.config.DatasourceConfig.Connection;
import io.github.siakhooi.query.config.QueryConfig;
import io.github.siakhooi.query.config.QueryConfig.Query;
import io.github.siakhooi.query.domain.DatasourceConnection;
import io.github.siakhooi.query.service.DatasourceConnectionService;

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
        Map<String, List<Map<String, Object>>> results = HashMap.newHashMap(queries.size());
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Map.Entry<String, List<Map<String, Object>>>>> futures = new ArrayList<>(queries.size());
            for (Query query : queries) {
                futures.add(executor.submit(() -> runQuery(query)));
            }
            for (Future<Map.Entry<String, List<Map<String, Object>>>> future : futures) {
                mergeFutureResult(future, results);
            }
        }

        return results;
    }

    private Map.Entry<String, List<Map<String, Object>>> runQuery(Query query) {
        Connection connection = resolveConnection(query);
        DatasourceConnection dc = dcs.getConnection(connection);
        List<Map<String, Object>> rows = executeQueryAgainstConnection(connection, query, dc);
        return Map.entry(query.name(), rows);
    }

    private Connection resolveConnection(Query query) {
        try {
            return datasourceConfig.getConnection(query.connection(), query.name());
        } catch (Exception e) {
            log.error("Error fetching connection for query: {}", query.name(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Error fetching connection for query: %s", query.name()));
        }
    }

    private static List<Map<String, Object>> executeQueryAgainstConnection(
            Connection connection, Query query, DatasourceConnection dc) {
        return switch (connection.getType().toLowerCase()) {
            case "mongodb" -> dc.execute(null, query.mongoQuery());
            default -> dc.execute(query.queryString(), null);
        };
    }

    private static void mergeFutureResult(
            Future<Map.Entry<String, List<Map<String, Object>>>> future,
            Map<String, List<Map<String, Object>>> results) {
        try {
            Map.Entry<String, List<Map<String, Object>>> entry = future.get();
            results.put(entry.getKey(), entry.getValue());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Interrupted", e);
        } catch (ExecutionException e) {
            rethrowExecutionCause(e);
        }
    }

    private static void rethrowExecutionCause(ExecutionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof ResponseStatusException rse) {
            throw rse;
        }
        if (cause instanceof RuntimeException re) {
            throw re;
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Query execution failed", cause);
    }

}
