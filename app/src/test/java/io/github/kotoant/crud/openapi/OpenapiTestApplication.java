package io.github.kotoant.crud.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class
})
@Profile("openapi-test")
public class OpenapiTestApplication {
    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("https://openapi-test.crud.kotoant.github.io");
        return new OpenAPI().servers(List.of(server));
    }
}
