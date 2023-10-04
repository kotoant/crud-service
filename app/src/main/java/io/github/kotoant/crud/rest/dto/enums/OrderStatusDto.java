package io.github.kotoant.crud.rest.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус заказа", enumAsRef = true)
public enum OrderStatusDto {
    UNKNOWN, ACTIVE, INACTIVE
}
