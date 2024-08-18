package site.tteolione.tteolione.api.service.product.response;

import lombok.Builder;
import lombok.Getter;
import site.tteolione.tteolione.api.service.product.dto.CategoryProductDto;

import java.util.List;

@Getter
@Builder
public class GetSimpleProductRes {

    private List<CategoryProductDto> list;

    @Builder
    public GetSimpleProductRes(List<CategoryProductDto> list) {
        this.list = list;
    }

    public static GetSimpleProductRes from(List<CategoryProductDto> categoryProductDtos) {
        return GetSimpleProductRes.builder()
                .list(categoryProductDtos)
                .build();
    }
}
