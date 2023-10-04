package io.github.kotoant.crud.test;

import io.github.kotoant.crud.config.Config;
import io.github.kotoant.crud.jooq.crud_app.Tables;
import io.github.kotoant.crud.jooq.crud_app.tables.daos.OrderDao;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Clock;
import java.util.function.Supplier;

import static io.github.kotoant.crud.jooq.crud_app.Tables.ORDER;
import static org.mockito.Mockito.when;


public class BaseTest extends BaseTestcontainersTest {


    @Autowired
    protected DSLContext ctx;

    @Autowired
    protected OrderDao orderDao;

    @Autowired
    protected WebTestClient webClient;

    @MockBean
    protected Supplier<Clock> clockSupplier;

    @BeforeEach
    void setUpBase() {
        ctx.deleteFrom(ORDER).execute();
        // default behavior
        when(clockSupplier.get()).thenReturn(Config.MOSCOW_CLOCK);
    }
}
