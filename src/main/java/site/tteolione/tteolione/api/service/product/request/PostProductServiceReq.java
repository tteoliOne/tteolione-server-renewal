package site.tteolione.tteolione.api.service.product.request;

import lombok.Builder;
import lombok.Getter;
import site.tteolione.tteolione.domain.category.Category;
import site.tteolione.tteolione.domain.product.Product;
import site.tteolione.tteolione.domain.product.constants.EProductSoldStatus;
import site.tteolione.tteolione.domain.user.User;

import java.time.LocalDateTime;

@Getter
public class PostProductServiceReq {
    private final Long categoryId;
    private final String title;
    private final int buyPrice;
    private final int buyCount;
    private final int sharePrice;
    private final int shareCount;
    private final LocalDateTime buyDate;
    private final String content;
    private final double longitude;
    private final double latitude;

    @Builder
    public PostProductServiceReq(Long categoryId, String title, int buyPrice, int buyCount, int sharePrice, int shareCount, LocalDateTime buyDate, String content, double longitude, double latitude) {
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

    public Product toEntity(User user, Category category) {
        return Product.builder()
                .title(title)
                .buyPrice(buyPrice)
                .buyCount(buyCount)
                .sharePrice(sharePrice)
                .shareCount(shareCount)
                .buyDate(buyDate)
                .content(content)
                .longitude(longitude)
                .latitude(latitude)
                .likeCount(0)
                .soldStatus(EProductSoldStatus.eNew)
                .category(category)
                .user(user)
                .build();
    }
}
