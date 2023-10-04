package io.github.kotoant.crud.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

@Builder(toBuilder = true)
@Schema(description = "дата")
public record DateDto(
        @NotNull
        @Schema(description = "год")
        Integer year,
        @NotNull
        @Range(min = 1, max = 12)
        @Schema(description = "месяц", minimum = "1", maximum = "12")
        Integer month,
        @NotNull
        @Range(min = 1, max = 31)
        @Schema(description = "день", minimum = "1", maximum = "31")
        Integer day
) {
}
