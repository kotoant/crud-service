package io.github.kotoant.crud.rest.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Фильтр по статусу", enumAsRef = true)
public enum StatusFilterDto {
    ACTIVE, ALL
}
