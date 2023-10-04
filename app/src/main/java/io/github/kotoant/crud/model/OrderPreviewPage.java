package io.github.kotoant.crud.model;

import java.util.List;

public record OrderPreviewPage(
        OrderPreviewPageToken nextPageToken,
        OrderPreviewPageToken prevPageToken,
        List<OrderPreview> content) {
}
