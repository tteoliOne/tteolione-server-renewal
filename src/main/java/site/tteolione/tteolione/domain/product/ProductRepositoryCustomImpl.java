package site.tteolione.tteolione.domain.product;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.tteolione.tteolione.domain.category.Category;
import site.tteolione.tteolione.domain.file.QFile;
import site.tteolione.tteolione.domain.file.constants.EPhotoType;
import site.tteolione.tteolione.domain.likes.QLikes;
import site.tteolione.tteolione.domain.product.constants.EProductSoldStatus;
import site.tteolione.tteolione.domain.user.User;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findByCategoryAndLikesWithFiles(User user, Category category, EProductSoldStatus soldStatus, EPhotoType photoType) {
        List<Tuple> result = jpaQueryFactory
                .select(
                        QProduct.product,
                        QLikes.likes.count(),
                        QLikes.likes.user.eq(user).as("liked")
                )
                .from(QProduct.product)
                .leftJoin(QProduct.product.likes, QLikes.likes)
                .leftJoin(QProduct.product.images, QFile.file).fetchJoin()
                .where(
                        QProduct.product.category.eq(category)
                                .and(QProduct.product.soldStatus.eq(soldStatus))
                )
                .groupBy(QProduct.product.productId, QFile.file.fileId)
                .having(
                        QFile.file.fileId.eq(JPAExpressions.select(QFile.file.fileId.min())
                                .from(QFile.file)
                                .where(QFile.file.product.eq(QProduct.product)
                                        .and(QFile.file.type.eq(photoType))
                                )))
                .orderBy(QProduct.product.createdDateTime.desc())
                .limit(5)
                .fetch();

        return result;
    }
}
