package io.github.kotoant.crud.repository;

import io.github.kotoant.crud.model.OrderPreview;
import io.github.kotoant.crud.model.OrderPreviewPage;
import io.github.kotoant.crud.model.OrderPreviewPageToken;
import io.github.kotoant.crud.model.StatusFilter;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Records;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static io.github.kotoant.crud.jooq.crud_app.Tables.ORDER;

@Repository
@RequiredArgsConstructor
public class OrderRepository {


    private final DSLContext ctx;


    public OrderPreviewPage searchOrders(Instant beginInclusive, Instant endExclusive,
                                         StatusFilter statusFilter,
                                         int pageSize,
                                         OrderPreviewPageToken pageToken) {
        Condition condition = DSL.noCondition();
        condition = condition.and(ORDER.LATEST_VERSION.isTrue());
        if (beginInclusive != null) {
            condition = condition.and(ORDER.CREATED.ge(beginInclusive));
        }
        if (endExclusive != null) {
            condition = condition.and(ORDER.CREATED.lt(endExclusive));
        }
        if (statusFilter == StatusFilter.ACTIVE) {
            condition = condition.and(ORDER.STATUS.eq(1));
        }
        OrderPreviewPageToken prevPageToken = null;
        int currentPageSize = pageSize;
        if (pageToken != null) {
            // https://2017.jpoint.ru/talks/hidden-complexity-of-a-routine-task-presenting-table-data-in-user-interface/
            Condition prevPageCondition = condition.and(ORDER.CREATED.ge(pageToken.created()))
                    .and(ORDER.CREATED.gt(pageToken.created()).or(ORDER.PUBLIC_ID.gt(pageToken.publicId())));

            condition = condition.and(ORDER.CREATED.le(pageToken.created()))
                    .and(ORDER.CREATED.lt(pageToken.created()).or(ORDER.PUBLIC_ID.le(pageToken.publicId())));

            List<Record2<Instant, UUID>> prevPage = ctx.select(ORDER.CREATED, ORDER.PUBLIC_ID)
                    .from(ORDER)
                    .where(prevPageCondition)
                    .orderBy(ORDER.CREATED, ORDER.PUBLIC_ID)
                    .limit(pageSize)
                    .fetch();

            if (!prevPage.isEmpty()) {
                int prevPageSize = prevPage.size();
                Record2<Instant, UUID> record = prevPage.get(prevPageSize - 1);
                prevPageToken = new OrderPreviewPageToken(record.value1(), record.value2(), prevPageSize);
            }

            if (pageToken.pageSize() != null) {
                currentPageSize = Math.min(currentPageSize, pageToken.pageSize());
            }
        }
        int pageSizePlusOne = currentPageSize + 1;
        List<OrderPreview> result = ctx.select(
                        ORDER.ID, ORDER.CREATED, ORDER.PUBLIC_ID, ORDER.VERSION, ORDER.STATUS
                )
                .from(ORDER)
                .where(condition)
                .orderBy(ORDER.CREATED.desc(), ORDER.PUBLIC_ID.desc())
                .limit(pageSizePlusOne)
                .fetch(Records.mapping(OrderPreview::new));

        OrderPreviewPageToken nextPageToken = null;
        if (result.size() == pageSizePlusOne) {
            OrderPreview last = result.get(result.size() - 1);
            nextPageToken = new OrderPreviewPageToken(last.created(), last.publicId(), null);
        }
        return new OrderPreviewPage(nextPageToken, prevPageToken, result.subList(0, Math.min(currentPageSize, result.size())));
    }
}
