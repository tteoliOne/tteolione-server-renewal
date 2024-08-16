package site.tteolione.tteolione.domain.product;

import com.querydsl.core.Tuple;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import site.tteolione.tteolione.IntegrationTestSupport;
import site.tteolione.tteolione.domain.category.Category;
import site.tteolione.tteolione.domain.category.CategoryRepository;
import site.tteolione.tteolione.domain.file.File;
import site.tteolione.tteolione.domain.file.FileRepository;
import site.tteolione.tteolione.domain.file.constants.EPhotoType;
import site.tteolione.tteolione.domain.likes.Likes;
import site.tteolione.tteolione.domain.likes.LikesRepository;
import site.tteolione.tteolione.domain.likes.QLikes;
import site.tteolione.tteolione.domain.product.constants.EProductSoldStatus;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ProductRepositoryCustomImplTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LikesRepository likesRepository;

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @SpyBean
    private AuditingHandler auditingHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }


    @AfterEach
    void tearDown() {
        likesRepository.deleteAllInBatch();
        fileRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("카테고리별 판매중인 상품 최신 5개조회(좋아유 유무 포함)")
    @Test
    void findByCategoryAndLikesWithFiles() {
        // given
        LocalDateTime previousDateTime = LocalDateTime.MAX;

        User user1 = createUser("test123", "test123@naver.com");
        User user2 = createUser("test1234", "test1234@naver.com");
        User user3 = createUser("test1235", "test12345@naver.com");
        userRepository.saveAll(List.of(user1, user2, user3));

        Category category1 = categoryRepository.findById(1L).get();

        createAndSaveProducts(user1, category1, 10);

        // when
        List<Tuple> findCategory1Products = productRepository.findByCategoryAndLikesWithFiles(user1, category1, EProductSoldStatus.eNew, EPhotoType.eProduct);

        // then
        Assertions.assertThat(findCategory1Products).hasSize(5);

        for (Tuple findCategory1Product : findCategory1Products) {
            Product findProduct = findCategory1Product.get(QProduct.product);
            Long likesCount = findCategory1Product.get(QLikes.likes.count());
            Boolean isLiked = findCategory1Product.get(QLikes.likes.user.eq(user1).as("liked"));


            Assertions.assertThat(findProduct.getImages()).hasSize(1);
            Assertions.assertThat(findProduct.getImages().get(0).getFileUrl()).isEqualTo("1.jpg");
            Assertions.assertThat(likesCount.intValue()).isEqualTo(1);
            Assertions.assertThat(isLiked).isTrue();
            Assertions.assertThat(findProduct.getCreatedDateTime()).isBeforeOrEqualTo(previousDateTime);
        }

    }

    private void createAndSaveProducts(User user, Category category, int count) {
        List<File> files = new ArrayList<>();
        List<Likes> likes = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            LocalDateTime testTime1 = LocalDateTime.of(2024, 10, 5, 0, i);
            BDDMockito.when(dateTimeProvider.getNow()).thenReturn(Optional.of(testTime1));

            Product product;
            // 새로운 상품 생성
            if (i % 2 == 0) {
                // 짝수 번째 상품은 "품절" 상태
                product = createNewProduct(user, category, EProductSoldStatus.eSoldOut, "테스트내용" + i);
            } else {
                // 홀수 번째 상품은 "신상품" 상태
                product = createNewProduct(user, category, EProductSoldStatus.eNew, "테스트내용" + i);
            }

            productRepository.save(product);

            // 각 상품에 대해 5개의 파일 생성
            for (int j = 1; j <= 5; j++) {
                String fileUrl = j + ".jpg"; // 파일 URL 예시
                EPhotoType photoType = (j != 5) ? EPhotoType.eProduct : EPhotoType.eReceipt; // 타입 예시
                File file = File.create(fileUrl, product, photoType);
                files.add(file); // 파일을 리스트에 추가
            }

            Likes like = Likes.builder()
                    .product(product)
                    .user(user)
                    .build();
            likes.add(like);
        }

        // 한 번에 파일 저장
        fileRepository.saveAll(files);

        // 한 번에 좋아요 저장
        likesRepository.saveAll(likes);
    }

    private User createUser(String username, String email) {
        return User.builder()
                .loginId(username)
                .email(email)
                .build();
    }

    private Product createNewProduct(User user1, Category category1, EProductSoldStatus soldStatus, String content) {
        return Product.builder()
                .user(user1)
                .category(category1)
                .content(content)
                .soldStatus(soldStatus)
                .build();
    }
}