package io.github.kotoant.crud.service;

import io.github.kotoant.crud.model.Date;
import io.github.kotoant.crud.model.OrderPreviewPage;
import io.github.kotoant.crud.model.Period;
import io.github.kotoant.crud.model.SearchOrdersFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderTransactionalService orderTransactionalService;
    private final Supplier<Clock> clockSupplier;


    public OrderPreviewPage searchOrders(SearchOrdersFilter filter) {
        Instant beginInclusive = null;
        Instant endExclusive = null;
        Period period = filter.period();
        if (period != null) {
            Clock clock = clockSupplier.get();
            ZoneId zoneId = clock.getZone();
            Date begin = period.begin();
            if (begin != null) {
                beginInclusive = ZonedDateTime.of(begin.year(), begin.month(), begin.day(), 0, 0, 0, 0, zoneId)
                        .toInstant();
            }
            Date end = period.end();
            if (end != null) {
                endExclusive = ZonedDateTime.of(end.year(), end.month(), end.day(), 0, 0, 0, 0, zoneId)
                        .plusDays(1).toInstant();
            }
        }
        return orderTransactionalService.searchOrders(
                beginInclusive, endExclusive, filter.statusFilter(), filter.pageSize(), filter.pageToken()
        );
    }
}
