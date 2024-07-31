package site.tteolione.tteolione.domain.file;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.domain.BaseEntity;
import site.tteolione.tteolione.domain.file.constants.EPhotoType;
import site.tteolione.tteolione.domain.product.Product;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private EPhotoType type;

    @Builder
    public File(String fileUrl, Product product, EPhotoType type) {
        this.fileUrl = fileUrl;
        this.product = product;
        this.type = type;
    }

    public static File create(String fileUrl, Product product, EPhotoType type) {
        return File.builder()
                .fileUrl(fileUrl)
                .product(product)
                .type(type)
                .build();
    }
}
