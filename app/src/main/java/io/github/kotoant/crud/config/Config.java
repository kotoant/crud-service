package io.github.kotoant.crud.config;

import io.github.kotoant.crud.jooq.crud_app.tables.daos.OrderDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;
import java.util.function.Supplier;

@Configuration
public class Config {
    public static final Clock MOSCOW_CLOCK = Clock.system(ZoneId.of("Europe/Moscow"));

    @Bean
    public Supplier<Clock> clockSupplier() {
        return () -> MOSCOW_CLOCK;
    }

    @Bean
    public OrderDao orderDao(org.jooq.Configuration configuration) {
        return new OrderDao(configuration);
    }

}
