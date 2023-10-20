package io.github.kotoant.crud.rest.dto;

import io.github.kotoant.crud.rest.dto.enums.OrderStatusDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
@Schema(description = "Превью заказа")
public record OrderPreviewDto(
        @Schema(description = "Дата и время создания заказа", example = "12:08, 23.10.2023")
        String created,
        @Schema(description = "Идентификатор заказа", format = "UUID", example = "609c8abc-0882-485c-a03c-272032a41132")
        UUID id,
        @Schema(description = "Статус заказа", enumAsRef = true, example = "ACTIVE")
        OrderStatusDto status
) {
}
