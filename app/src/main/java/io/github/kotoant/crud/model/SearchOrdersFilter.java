package io.github.kotoant.crud.model;

public record SearchOrdersFilter(
        Period period,
        StatusFilter statusFilter,
        int pageSize,
        OrderPreviewPageToken pageToken
) {
}
