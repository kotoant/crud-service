package io.github.kotoant.crud.converter;

import io.github.kotoant.crud.model.OrderPreview;
import io.github.kotoant.crud.model.OrderPreviewPage;
import io.github.kotoant.crud.model.OrderPreviewPageToken;
import io.github.kotoant.crud.rest.dto.OrderPreviewDto;
import io.github.kotoant.crud.rest.dto.SearchOrdersResponseDto;
import io.github.kotoant.crud.rest.dto.enums.OrderStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CrudConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy");

    private final Supplier<Clock> clockSupplier;

    public SearchOrdersResponseDto toDto(OrderPreviewPage page) {
        List<OrderPreviewDto> content = page.content().stream().map(this::previewToDto).toList();
        return new SearchOrdersResponseDto(tokenToDto(page.nextPageToken()), tokenToDto(page.prevPageToken()), content);
    }


    public OrderPreviewDto previewToDto(OrderPreview preview) {
        return new OrderPreviewDto(instantToDto(preview.created()), preview.publicId(), statusToDto(preview.status())
        );
    }

    // 12:05, 21.03.2023 in Moscow time zone
    public String instantToDto(Instant instant) {
        if (instant == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(instant, clockSupplier.get().getZone()));
    }

    private static OrderStatusDto statusToDto(Integer status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case 1 -> OrderStatusDto.ACTIVE;
            case 2 -> OrderStatusDto.INACTIVE;
            default -> OrderStatusDto.UNKNOWN;
        };
    }

    private static String tokenToDto(OrderPreviewPageToken token) {
        return OrderPreviewPageToken.toToken(token);
    }
}
