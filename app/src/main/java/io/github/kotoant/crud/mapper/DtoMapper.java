package io.github.kotoant.crud.mapper;

import io.github.kotoant.crud.model.OrderPreviewPageToken;
import io.github.kotoant.crud.model.SearchOrdersFilter;
import io.github.kotoant.crud.rest.dto.SearchOrdersRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = CentralConfig.class)
public interface DtoMapper {
    @Mapping(target = "pageToken", source = "request.pageToken", qualifiedByName = "pageTokenToModel")
    SearchOrdersFilter toModel(SearchOrdersRequestDto request);

    @Named("pageTokenToModel")
    static OrderPreviewPageToken pageTokenToModel(String token) {
        return OrderPreviewPageToken.fromToken(token);
    }


}
