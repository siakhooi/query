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

    public QueryController(GreetingConfig config) {
        this.config = config;
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
}
