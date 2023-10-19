package io.github.kotoant.crud.test;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;

@Slf4j
@SuppressWarnings("rawtypes")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BaseTestcontainersTest {
    private static final String POSTGRES_IMAGE_NAME = "postgres:15.2";
    private static final long WAIT_TIME_SECONDS = Long.parseLong(System.getProperty("postgres.test.wait-time-seconds", "2"));
    private static final PostgreSQLContainer POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE_NAME).asCompatibleSubstituteFor("postgres"))
                    .withReuse(true)
                    // keep in sync with 'scripts/calc-local-docker-env.sh' and 'scripts/stop-and-remove-testcontainers.sh'
                    .withLabel("io.github.kotoant.crud.testcontainers.postgres", "crud")
                    .withStartupTimeout(Duration.ofMinutes(5))
                    .withDatabaseName("crud")
                    .withUsername("crud_app")
                    .withPassword("crud_app")
                    .withCopyFileToContainer(MountableFile.forClasspathResource("db/init.sql"),
                            "/docker-entrypoint-initdb.d/init.sql");


    static {
        POSTGRESQL_CONTAINER.start();
        var dbPort = POSTGRESQL_CONTAINER.getMappedPort(POSTGRESQL_PORT);
        log.info("Postgresql container started. Address: {}, port: {}", POSTGRESQL_CONTAINER.getHost(), dbPort);

        String dbHost = POSTGRESQL_CONTAINER.getHost() + ":" + dbPort;
        String dbName = POSTGRESQL_CONTAINER.getDatabaseName();
        String dbUsername = POSTGRESQL_CONTAINER.getUsername();
        String dbPassword = POSTGRESQL_CONTAINER.getPassword();

        System.setProperty("DB_HOST", dbHost);
        System.setProperty("DB_NAME", dbName);
        System.setProperty("DB_USERNAME", dbUsername);
        System.setProperty("DB_PASSWORD", dbPassword);

        waitForPostgres();

        // liquibase
        try (Connection connection = DriverManager.getConnection(
                String.format("jdbc:postgresql://%s/%s?currentSchema=crud_app", dbHost, dbName), dbUsername, dbPassword)) {

            var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setDefaultSchemaName("crud_app");
            Liquibase liquibase = new Liquibase("db/changelog/database_changelog.xml",
                    new ClassLoaderResourceAccessor(), database
            );
            liquibase.updateTestingRollback("prod");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void waitForPostgres() {
        try {
            Thread.sleep(1000 * WAIT_TIME_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
