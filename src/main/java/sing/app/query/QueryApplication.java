package sing.app.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.cassandra.autoconfigure.CassandraAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		MongoAutoConfiguration.class,
		CassandraAutoConfiguration.class
})
public class QueryApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueryApplication.class, args);
	}

}
