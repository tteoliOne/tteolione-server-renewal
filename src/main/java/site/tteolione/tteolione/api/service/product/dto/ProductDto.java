package site.tteolione.tteolione.api.service.product.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.domain.product.constants.EProductSoldStatus;

@Data
@NoArgsConstructor
public class ProductDto {

    private long productId;
    private String imageUrl;
    private String title;
    private int unitPrice;
    private double walkingDistance;
    private int walkingTime;
    private int totalLikes;
    private String soldStatus;
    private boolean liked;

    @Builder
    public ProductDto(long productId, String imageUrl, String title, int unitPrice, double walkingDistance, int walkingTime, int totalLikes, EProductSoldStatus soldStatus, Long likeId, boolean liked) {
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.unitPrice = unitPrice;
        this.walkingDistance = walkingDistance;
        this.walkingTime = walkingTime;
        this.totalLikes = totalLikes;
        this.soldStatus = soldStatus.toString();
        this.liked = liked;
    }

}
