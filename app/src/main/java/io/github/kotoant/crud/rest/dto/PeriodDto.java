package io.github.kotoant.crud.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "период")
public record PeriodDto(
        @Valid
        @Schema(description = "начало", nullable = true)
        DateDto begin,
        @Valid
        @Schema(description = "конец", nullable = true)
        DateDto end
) {
}
