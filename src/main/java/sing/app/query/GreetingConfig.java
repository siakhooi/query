package sing.app.query;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
@ConfigurationProperties(prefix = "app")
public class GreetingConfig {
    private String defaultGreetingMessage;

    public String getDefaultMessage() {
        return getDefaultGreetingMessage();
    }
}
