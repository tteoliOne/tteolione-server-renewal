package site.tteolione.tteolione.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.domain.BaseEntity;
import site.tteolione.tteolione.domain.category.Category;
import site.tteolione.tteolione.domain.file.File;
import site.tteolione.tteolione.domain.likes.Likes;
import site.tteolione.tteolione.domain.product.constants.EProductSoldStatus;
import site.tteolione.tteolione.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String title;
    private String content;

    private int buyPrice;
    private int buyCount;
    private LocalDateTime buyDate;

    private int sharePrice;
    private int shareCount;
    private int totalCount;

    private int likeCount;
    private double longitude;
    private double latitude;

    @Enumerated(EnumType.STRING)
    private EProductSoldStatus soldStatus;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<File> images = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Likes> likes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Product(String title, String content, int buyPrice, int buyCount, LocalDateTime buyDate, int sharePrice, int shareCount, int totalCount, int likeCount, double longitude, double latitude, EProductSoldStatus soldStatus, List<File> images, List<Likes> likes, User user, Category category) {
        this.title = title;
        this.content = content;
        this.buyPrice = buyPrice;
        this.buyCount = buyCount;
        this.buyDate = buyDate;
        this.sharePrice = sharePrice;
        this.shareCount = shareCount;
        this.totalCount = totalCount;
        this.likeCount = likeCount;
        this.longitude = longitude;
        this.latitude = latitude;
        this.soldStatus = soldStatus;
        this.images = images;
        this.likes = likes;
        this.user = user;
        this.category = category;
    }
}
