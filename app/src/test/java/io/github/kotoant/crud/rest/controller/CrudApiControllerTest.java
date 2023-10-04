package io.github.kotoant.crud.rest.controller;

import io.github.kotoant.crud.jooq.crud_app.tables.pojos.Order;
import io.github.kotoant.crud.rest.dto.SearchOrdersRequestDto;
import io.github.kotoant.crud.rest.dto.SearchOrdersResponseDto;
import io.github.kotoant.crud.rest.dto.enums.StatusFilterDto;
import io.github.kotoant.crud.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


@Slf4j
class CrudApiControllerTest extends BaseTest {

    private void insertOrderData() {
        Instant now = Instant.now();
        orderDao.insert(List.of(
                new Order(1L, UUID.randomUUID(), 1, true, now, now, now, 1),
                new Order(2L, UUID.randomUUID(), 1, true, now, now, now, 1),
                new Order(3L, UUID.randomUUID(), 1, true, now, now, now, 1),
                new Order(4L, UUID.randomUUID(), 1, true, now, now, now, 1),
                new Order(5L, UUID.randomUUID(), 1, true, now, now, now, 1),
                new Order(6L, UUID.randomUUID(), 1, true, now, now, now, 1),
                new Order(7L, UUID.randomUUID(), 1, true, now, now, now, 1),
                new Order(8L, UUID.randomUUID(), 1, true, now, now, now, 1),
                new Order(9L, UUID.randomUUID(), 1, true, now, now, now, 1),
                new Order(10L, UUID.randomUUID(), 1, true, now, now, now, 1)
        ));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @Timeout(20)
    void searchOrders_Pagination(int pageSize) throws Exception {
        insertOrderData();

        scrollForwardAndBack(pageSize, 10);
    }

    private void scrollForwardAndBack(int pageSize, int expectedElementsCount) throws Exception {
        SearchOrdersRequestDto request = SearchOrdersRequestDto.builder()
                .statusFilter(StatusFilterDto.ALL)
                .pageSize(pageSize)
                .build();
        String pageToken = null;
        String prevPageToken;
        int lastPageContentSize;
        int actualElementsCount = 0;
        do {
            SearchOrdersResponseDto response = webClient
                    .post()
                    .uri("/api/orders/search")
                    .bodyValue(request.toBuilder().pageToken(pageToken).build())
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(SearchOrdersResponseDto.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(response).isNotNull();
            if (pageToken == null) {
                assertThat(response.prevPageToken()).isNull();
            } else {
                assertThat(response.prevPageToken()).isNotNull();
            }
            prevPageToken = response.prevPageToken();

            lastPageContentSize = response.content().size();

            actualElementsCount += response.content().size();

            if (actualElementsCount < expectedElementsCount) {
                assertThat(response.nextPageToken()).isNotNull();
                assertThat(response.content()).hasSize(pageSize);
            } else if (actualElementsCount == expectedElementsCount) {
                assertThat(response.nextPageToken()).isNull();
                assertThat(response.content()).hasSizeBetween(1, pageSize);
            } else {
                fail("actualElementsCount > expectedElementsCount, [actualElementsCount: %d, expectedElementsCount: %d]",
                        actualElementsCount, expectedElementsCount);
            }

            pageToken = response.nextPageToken();
        } while (pageToken != null);
        assertThat(actualElementsCount).isEqualTo(expectedElementsCount);

        pageToken = prevPageToken;

        if (pageSize >= expectedElementsCount) {
            assertThat(pageToken).isNull();
        } else {
            assertThat(pageToken).isNotNull();

            // add one extra order for scroll back
            Instant now = Instant.now();
            orderDao.insert(new Order(11L, UUID.randomUUID(), 1, true, now, now, now, 1));
            expectedElementsCount++;

            actualElementsCount = lastPageContentSize;
            do {
                SearchOrdersResponseDto response = webClient
                        .post()
                        .uri("/api/orders/search")
                        .bodyValue(request.toBuilder().pageToken(pageToken).build())
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(SearchOrdersResponseDto.class)
                        .returnResult()
                        .getResponseBody();

                assertThat(response).isNotNull();
                assertThat(response.nextPageToken()).isNotNull();

                actualElementsCount += response.content().size();

                if (actualElementsCount < expectedElementsCount) {
                    assertThat(response.prevPageToken()).isNotNull();
                    assertThat(response.content()).hasSize(pageSize);
                } else if (actualElementsCount == expectedElementsCount) {
                    assertThat(response.prevPageToken()).isNull();
                    assertThat(response.content()).hasSizeBetween(1, pageSize);
                } else {
                    fail("actualElementsCount > expectedElementsCount, [actualElementsCount: %d, expectedElementsCount: %d]",
                            actualElementsCount, expectedElementsCount);
                }

                pageToken = response.prevPageToken();
            } while (pageToken != null);
            assertThat(actualElementsCount).isEqualTo(expectedElementsCount);
        }
    }
}