package io.github.kotoant.crud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jooq.JSONB;
import org.jooq.exception.DataTypeException;
import org.jooq.jackson.extensions.converters.JSONBtoJacksonConverter;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

public record OrderPreviewPageToken(
        @JsonProperty("created") Instant created,
        @JsonProperty("publicId") UUID publicId,
        @JsonProperty("pageSize") Integer pageSize
) {
    private static final JSONBtoJacksonConverter<OrderPreviewPageToken> CONVERTER =
            new JSONBtoJacksonConverter<>(OrderPreviewPageToken.class);

    public static OrderPreviewPageToken fromToken(String token) {
        if (token == null) {
            return null;
        }
        try {
            return CONVERTER.from(JSONB.jsonb(new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8)));
        } catch (DataTypeException e) {
            return null;
        }
    }

    public static String toToken(OrderPreviewPageToken token) {
        if (token == null) {
            return null;
        }
        return token.toToken();
    }

    public String toToken() {
        return Base64.getEncoder().encodeToString(CONVERTER.to(this).data().getBytes(StandardCharsets.UTF_8));
    }
}
