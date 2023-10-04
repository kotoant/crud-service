package io.github.kotoant.crud.rest.controller;

import io.github.kotoant.crud.converter.CrudConverter;
import io.github.kotoant.crud.mapper.DtoMapper;
import io.github.kotoant.crud.model.OrderPreviewPage;
import io.github.kotoant.crud.model.SearchOrdersFilter;
import io.github.kotoant.crud.rest.api.CrudApi;
import io.github.kotoant.crud.rest.dto.SearchOrdersRequestDto;
import io.github.kotoant.crud.rest.dto.SearchOrdersResponseDto;
import io.github.kotoant.crud.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CrudApiController implements CrudApi {


    private final OrderService orderService;
    private final DtoMapper dtoMapper;
    private final CrudConverter converter;

    @Override
    public ResponseEntity<SearchOrdersResponseDto> searchOrders(SearchOrdersRequestDto request) {
        log.info("received request: {}", request);
        SearchOrdersFilter filter = dtoMapper.toModel(request);
        OrderPreviewPage page = orderService.searchOrders(filter);
        SearchOrdersResponseDto response = converter.toDto(page);
        if (log.isDebugEnabled()) {
            log.debug("sending response: {}", response);
        } else {
            log.info("sending response: [nextPageToken: {}, prevPageToken: {}, content.size: {}]",
                    response.nextPageToken(), response.prevPageToken(), response.content().size());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
