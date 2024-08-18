package site.tteolione.tteolione.api.service.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
public class CategoryProductDto {

    private Long categoryId;
    private String categoryName;
    private List<ProductDto> products;

    @Builder
    public CategoryProductDto(Long categoryId, String categoryName, List<ProductDto> products) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.products = products;
    }
}
