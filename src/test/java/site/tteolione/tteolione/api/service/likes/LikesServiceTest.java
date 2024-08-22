package site.tteolione.tteolione.api.service.likes;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import site.tteolione.tteolione.IntegrationTestSupport;
import site.tteolione.tteolione.domain.likes.Likes;
import site.tteolione.tteolione.domain.likes.LikesRepository;
import site.tteolione.tteolione.domain.product.Product;
import site.tteolione.tteolione.domain.product.ProductRepository;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class LikesServiceTest extends IntegrationTestSupport {

    @Autowired
    private LikesService likesService;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        likesRepository.deleteAllInBatch();
    }

    @Test
    void findByProductAndUser_Not_Exist() {
        Product product = Product.builder().build();
        productRepository.save(product);

        User user = User.builder().nickname("test1").build();
        userRepository.save(user);

        Likes findLike = likesService.findByProductAndUser(product, user);

        Assertions.assertThat(findLike).isNull();
    }

    @Test
    void findByProductAndUser_Exist() {
        Product product = Product.builder().build();
        productRepository.save(product);

        User user = User.builder().nickname("test1").build();
        userRepository.save(user);

        Likes like = Likes.builder()
                .product(product)
                .user(user)
                .build();
        likesService.createLike(like);

        Likes findLike = likesService.findByProductAndUser(product, user);
        Assertions.assertThat(findLike).isNotNull();
    }


    @Test
    void deleteByLike() {
        Product product = Product.builder().build();
        productRepository.save(product);

        User user = User.builder().nickname("test1").build();
        userRepository.save(user);

        Likes like = Likes.builder()
                .product(product)
                .user(user)
                .build();
        likesRepository.save(like);

        likesService.deleteByLike(like);

        Likes findLike = likesService.findByProductAndUser(product, user);
        Assertions.assertThat(findLike).isNull();
    }

    @Test
    void createLike() {
        Product product = Product.builder().build();
        productRepository.save(product);

        User user = User.builder().nickname("test1").build();
        userRepository.save(user);

        Likes like = Likes.builder()
                .product(product)
                .user(user)
                .build();
        likesService.createLike(like);

        Likes findLike = likesService.findByProductAndUser(product, user);
        Assertions.assertThat(findLike).isNotNull();
    }
}
