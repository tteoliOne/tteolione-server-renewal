package site.tteolione.tteolione.api.controller.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import site.tteolione.tteolione.api.service.product.request.PostProductServiceReq;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostProductReq {

    @NotNull(message = "카테고리 Id를 입력해주세요.")
    private Long categoryId;

    @NotBlank(message = "상품 제목을 입력해주세요.")
    private String title;

    @Positive(message = "구입 가격을 입력해주세요.")
    private int buyPrice;

    @Positive(message = "구입 수량을 입력해주세요.")
    private int buyCount;

    @Positive(message = "공유 가격을 입력해주세요.")
    private int sharePrice;

    @Positive(message = "공유 수량을 입력해주세요.")
    private int shareCount;

    @NotNull(message = "구매 일자를 입력해주세요.")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime buyDate;

    @NotBlank(message = "상세 설명을 입력해주세요.")
    private String content;

    @Positive(message = "경도를 입력해주세요.")
    private double longitude;

    @Positive(message = "위도를 입력해주세요.")
    private double latitude;

    @Builder
    public PostProductReq(@NotNull(message = "카테고리 Id를 입력해주세요.") Long categoryId, @NotNull(message = "상품 제목을 입력해주세요.") String title, @NotNull(message = "구입 가격을 입력해주세요.") int buyPrice, @NotNull(message = "구입 수량을 입력해주세요.") int buyCount, @NotNull(message = "공유 가격을 입력해주세요.") int sharePrice, @NotNull(message = "공유 수량을 입력해주세요.") int shareCount, @NotNull(message = "구매 일자를 입력해주세요.") LocalDateTime buyDate, @NotNull(message = "상세 설명을 입력해주세요.") String content, @NotNull(message = "경도를 입력해주세요.") double longitude, @NotNull(message = "위도를 입력해주세요.") double latitude) {
        this.categoryId = categoryId;
        this.title = title;
        this.buyPrice = buyPrice;
        this.buyCount = buyCount;
        this.sharePrice = sharePrice;
        this.shareCount = shareCount;
        this.buyDate = buyDate;
        this.content = content;
        this.longitude = longitude;
        this.latitude = latitude;
    }

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
