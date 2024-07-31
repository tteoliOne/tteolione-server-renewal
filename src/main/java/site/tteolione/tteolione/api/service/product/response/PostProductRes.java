package site.tteolione.tteolione.api.service.product.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import site.tteolione.tteolione.domain.product.Product;

@Getter
public class PostProductRes {
    private final Long productId;
    private final Long userId;
    private final String result;

    @Builder
    public PostProductRes(Long productId, Long userId, String result) {
        this.productId = productId;
        this.userId = userId;
        this.result = result;
    }

    @Builder
    public static PostProductRes from(Product product){
        return PostProductRes.builder()
                .productId(product.getProductId())
                .userId(product.getUser().getUserId())
                .result("정상적으로 게시되었습니다.")
                .build();
    }
}
