package sing.app.query.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import sing.app.query.Util;
import sing.app.query.config.DatasourceConfig;

@RestController
@Slf4j
public class DatasourceConfigController {

    private DatasourceConfig datasourceConfig;

    public DatasourceConfigController(DatasourceConfig datasourceConfig) {
        this.datasourceConfig = datasourceConfig;
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
