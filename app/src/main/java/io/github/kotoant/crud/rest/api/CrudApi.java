package io.github.kotoant.crud.rest.api;

import io.github.kotoant.crud.rest.dto.SearchOrdersRequestDto;
import io.github.kotoant.crud.rest.dto.SearchOrdersResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping("/api")
public interface CrudApi {

    @Operation(
            summary = "Search orders.",
            operationId = "searchOrders",
            description = "Search orders.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Response with search result.",
                            content = @Content(schema = @Schema(implementation = SearchOrdersResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request.",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "500", description = "Server error.",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            }
    )
    @Tag(name = "orders")
    @PostMapping(path = "/orders/search", consumes = "application/json", produces = "application/json")
    default ResponseEntity<SearchOrdersResponseDto> searchOrders(
            @Parameter(required = true) @Valid @RequestBody SearchOrdersRequestDto request) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
