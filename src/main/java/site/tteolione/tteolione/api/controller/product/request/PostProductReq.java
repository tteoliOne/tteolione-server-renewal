package site.tteolione.tteolione.api.controller.product.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.api.service.product.request.PostProductServiceReq;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostProductReq {

    @NotNull(message = "카테고리 Id를 입력해주세요.")
    private Long categoryId;

    @NotNull(message = "상품 제목을 입력해주세요.")
    private String title;

    @NotNull(message = "구입 가격을 입력해주세요.")
    private int buyPrice;

    @NotNull(message = "구입 수량을 입력해주세요.")
    private int buyCount;

    @NotNull(message = "공유 가격을 입력해주세요.")
    private int sharePrice;

    @NotNull(message = "공유 수량을 입력해주세요.")
    private int shareCount;

    @NotNull(message = "구매 일자를 입력해주세요.")
    private LocalDateTime buyDate;

    @NotNull(message = "상세 설명을 입력해주세요.")
    private String content;

    @NotNull(message = "경도를 입력해주세요.")
    private double longitude;

    @NotNull(message = "위도를 입력해주세요.")
    private double latitude;

    public PostProductServiceReq toServiceRequest() {
        return PostProductServiceReq.builder()
                .categoryId(categoryId)
                .title(title)
                .buyPrice(buyPrice)
                .buyCount(buyCount)
                .sharePrice(sharePrice)
                .shareCount(shareCount)
                .buyDate(buyDate)
                .content(content)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }
}
