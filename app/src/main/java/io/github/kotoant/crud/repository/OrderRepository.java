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
        // where "order"."latest_version" = true
        condition = condition.and(ORDER.LATEST_VERSION.isTrue());
        if (beginInclusive != null) {
            // and "order"."created" >= :beginInclusive
            condition = condition.and(ORDER.CREATED.greaterOrEqual(beginInclusive));
        }
        if (endExclusive != null) {
            // and "order"."created" < :endExclusive
            condition = condition.and(ORDER.CREATED.lessThan(endExclusive));
        }
        if (statusFilter == StatusFilter.ACTIVE) {
            // and "order"."status" = 1
            condition = condition.and(ORDER.STATUS.eq(1));
        }
        OrderPreviewPageToken prevPageToken = null;
        int currentPageSize = pageSize;
        if (pageToken != null) {
            // https://2017.jpoint.ru/talks/hidden-complexity-of-a-routine-task-presenting-table-data-in-user-interface/
            // условие для запроса предыдущей страницы:
            // and "order"."created" >= :pageTokenCreated and (
            //   "order"."created" > :pageTokenCreated or "order"."public_id" > :pageTokenPublicId
            // )
            Condition prevPageCondition = condition.and(ORDER.CREATED.greaterOrEqual(pageToken.created()))
                    .and(ORDER.CREATED.greaterThan(pageToken.created()).or(ORDER.PUBLIC_ID.greaterThan(pageToken.publicId())));

            // условие для запроса текущей страницы:
            // and "order"."created" <= :pageTokenCreated and (
            //   "order"."created" < :pageTokenCreated or "order"."public_id" <= :pageTokenPublicId
            // )
            condition = condition.and(ORDER.CREATED.lessOrEqual(pageToken.created()))
                    .and(ORDER.CREATED.lessThan(pageToken.created()).or(ORDER.PUBLIC_ID.lessOrEqual(pageToken.publicId())));

            List<Record2<Instant, UUID>> prevPage = ctx.select(ORDER.CREATED, ORDER.PUBLIC_ID)
                    .from(ORDER)
                    .where(prevPageCondition)
                    // порядок сортировки отличается от запроса текущей страницы, так как в этом случае пролистываем
                    // в противоположном направлении
                    .orderBy(ORDER.CREATED, ORDER.PUBLIC_ID)
                    .limit(pageSize)
                    .fetch();

            if (!prevPage.isEmpty()) { // если предыдущая страница есть, то вычисляем для нее токен
                int prevPageSize = prevPage.size();
                Record2<Instant, UUID> prevPageFirst = prevPage.get(prevPageSize - 1);
                // в токене для предыдущей страницы передаем ее размер, так как он может быть меньше, чем запросил
                // пользователь; это нужно, чтобы избежать дублей при пролистывании назад до первой страницы
                prevPageToken = new OrderPreviewPageToken(prevPageFirst.value1(), prevPageFirst.value2(), prevPageSize);
            }

            if (pageToken.pageSize() != null) {
                // если в токене указан размер страницы (запрос предыдущей страницы), то учитываем его при вычислении
                // размера текущей страницы
                currentPageSize = Math.min(currentPageSize, pageToken.pageSize());
            }
        }
        int pageSizePlusOne = currentPageSize + 1;
        List<OrderPreview> result = ctx.select(
                        ORDER.ID, ORDER.CREATED, ORDER.PUBLIC_ID, ORDER.VERSION, ORDER.STATUS
                )
                .from(ORDER)
                .where(condition)
                // порядок сортировки текущей страницы: сначала идут самые свежие заказы
                .orderBy(ORDER.CREATED.desc(), ORDER.PUBLIC_ID.desc())
                // запрашиваем на одну страницу больше, чтобы сразу за один запрос вычислить токен следующей страницы
                .limit(pageSizePlusOne)
                .fetch(Records.mapping(OrderPreview::new));

        OrderPreviewPageToken nextPageToken = null;
        if (result.size() == pageSizePlusOne) { // если следующая страница есть, то вычисляем для нее токен
            OrderPreview nextPageFirst = result.get(result.size() - 1);
            nextPageToken = new OrderPreviewPageToken(nextPageFirst.created(), nextPageFirst.publicId(), null);
        }
        return new OrderPreviewPage(nextPageToken, prevPageToken, result.subList(0, Math.min(currentPageSize, result.size())));
    }
}
