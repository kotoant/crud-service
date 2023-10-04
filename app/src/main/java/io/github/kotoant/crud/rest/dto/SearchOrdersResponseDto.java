package io.github.kotoant.crud.rest.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
@Schema(description = "Ответ на поиск заказов")
public record SearchOrdersResponseDto(
        @Schema(description = "Токен следующей страницы", nullable = true, example = "MTAwNTAwMDAwMDAwMA==")
        String nextPageToken,
        @Schema(description = "Токен предыдущей страницы", nullable = true, example = "MTIzNDU2Nzg5MA==")
        String prevPageToken,
        @ArraySchema(
                schema = @Schema(implementation = OrderPreviewDto.class),
                arraySchema = @Schema(description = "Список превью инцидентов")
        )
        List<OrderPreviewDto> content
) {
}
