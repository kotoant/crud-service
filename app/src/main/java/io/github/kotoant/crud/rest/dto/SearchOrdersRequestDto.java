package io.github.kotoant.crud.rest.dto;

import io.github.kotoant.crud.rest.dto.enums.StatusFilterDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;
import org.springframework.lang.NonNull;

@Builder(toBuilder = true)
@Schema(description = "Запрос на поиск заказов")
public record SearchOrdersRequestDto(
        @Valid
        @Schema(description = "период", nullable = true, defaultValue = "{}")
        PeriodDto period,
        @Schema(description = "Фильтр по статусу", enumAsRef = true, nullable = true, defaultValue = "ACTIVE", example = "ACTIVE")
        StatusFilterDto statusFilter,
        @Range(min = 1, max = 10)
        @Schema(description = "Размер страницы", minimum = "1", maximum = "10", nullable = true, defaultValue = "10", example = "10")
        Integer pageSize,
        @Schema(description = "Токен страницы", nullable = true, example = "MTAwNTAw")
        String pageToken
) {
    public static final int DEFAULT_PAGE_SIZE = 10;

    @NonNull
    @Override
    public PeriodDto period() {
        return period == null ? new PeriodDto(null, null) : period;
    }

    @NonNull
    @Override
    public StatusFilterDto statusFilter() {
        return statusFilter == null ? StatusFilterDto.ACTIVE : statusFilter;
    }

    @NonNull
    @Override
    public Integer pageSize() {
        return pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
    }
}
