package io.github.kotoant.crud.openapi;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static io.github.kotoant.crud.test.TestUtils.loadFile;
import static io.github.kotoant.crud.test.TestUtils.saveFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Order(1)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = OpenapiTestApplication.class,
        properties = {"springdoc.writer-with-default-pretty-printer=true", "springdoc.writer-with-order-by-keys=true"})
@ActiveProfiles("openapi-test")
public class OpenapiApplicationTest {
    private static final Path OPENAPI_DIR = Paths.get("openapi");
    private static final Path GENERATED_DIR = OPENAPI_DIR.resolve("generated");
    private static final String FILE_NAME = "api.yaml";

    @Autowired
    protected WebTestClient webClient;

    @Test
    void check_openapi_is_unchanged() {
        var actual = new String(Objects.requireNonNull(
                webClient
                        .get()
                        .uri("/v3/api-docs.yaml")
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBody()
        ));

        var generatedFile = GENERATED_DIR.resolve(FILE_NAME);
        saveFile(generatedFile, actual);

        var expected = loadFile(OPENAPI_DIR.resolve(FILE_NAME));
        assertEquals(expected, actual, "Generated file does not match reference file: " + FILE_NAME);
    }
}
