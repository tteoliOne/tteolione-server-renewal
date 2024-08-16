package site.tteolione.tteolione.domain.product;

import com.querydsl.core.Tuple;
import site.tteolione.tteolione.domain.category.Category;
import site.tteolione.tteolione.domain.file.constants.EPhotoType;
import site.tteolione.tteolione.domain.product.constants.EProductSoldStatus;
import site.tteolione.tteolione.domain.user.User;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Tuple> findByCategoryAndLikesWithFiles(User user, Category category, EProductSoldStatus soldStatus, EPhotoType photoType);
}
