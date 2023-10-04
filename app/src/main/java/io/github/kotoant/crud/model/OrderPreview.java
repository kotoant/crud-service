package io.github.kotoant.crud.model;

import java.time.Instant;
import java.util.UUID;

public record OrderPreview(
        long id,
        Instant created,
        UUID publicId,
        int version,
        Integer status
) {
}
