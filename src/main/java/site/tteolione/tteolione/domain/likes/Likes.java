package site.tteolione.tteolione.domain.likes;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.domain.product.Product;
import site.tteolione.tteolione.domain.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Version
    private Long version;

    @Builder
    public Likes(Product product, User user) {
        this.product = product;
        this.user = user;
    }

    public void removeLike() {
        this.product = null;
        this.user = null;
    }

    public void addLike(Product product) {
        this.product = product;
        this.user = product.getUser();
    }
}
