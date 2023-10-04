package io.github.kotoant.crud.service;

import io.github.kotoant.crud.model.OrderPreviewPage;
import io.github.kotoant.crud.model.OrderPreviewPageToken;
import io.github.kotoant.crud.model.StatusFilter;
import io.github.kotoant.crud.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OrderTransactionalService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public OrderPreviewPage searchOrders(Instant beginInclusive, Instant endExclusive,
                                         StatusFilter statusFilter,
                                         int pageSize,
                                         OrderPreviewPageToken pageToken) {
        return orderRepository.searchOrders(beginInclusive, endExclusive, statusFilter, pageSize, pageToken);
    }

}
