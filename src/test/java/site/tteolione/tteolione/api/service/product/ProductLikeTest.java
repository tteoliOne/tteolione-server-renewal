package site.tteolione.tteolione.api.service.product;

import com.querydsl.core.Tuple;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.IntegrationTestSupport;
import site.tteolione.tteolione.WithMockCustomAccount;
import site.tteolione.tteolione.api.service.category.CategoryService;
import site.tteolione.tteolione.api.service.product.request.PostProductServiceReq;
import site.tteolione.tteolione.api.service.product.response.GetSimpleProductRes;
import site.tteolione.tteolione.api.service.product.response.PostProductRes;
import site.tteolione.tteolione.api.service.user.UserService;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.common.util.SecurityUtils;
import site.tteolione.tteolione.domain.category.Category;
import site.tteolione.tteolione.domain.file.File;
import site.tteolione.tteolione.domain.file.constants.EPhotoType;
import site.tteolione.tteolione.domain.likes.QLikes;
import site.tteolione.tteolione.domain.product.Product;
import site.tteolione.tteolione.domain.product.ProductRepository;
import site.tteolione.tteolione.domain.product.QProduct;
import site.tteolione.tteolione.domain.product.constants.EProductSoldStatus;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;
import site.tteolione.tteolione.domain.user.constants.EAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ProductLikeTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
    }

    @DisplayName("상품 좋아요 동시성 테스트")
    @Test
    void likeProduct_Sync_Test() throws Exception{
        // given
        List<User> users = IntStream.range(1, 11)
                .mapToObj(i -> User.builder().nickname("user" + i).build())
                .collect(Collectors.toList());
        userRepository.saveAll(users);

        Product product = Product.builder()
                .user(users.get(0)) // 상품 소유자
                .build();
        productRepository.save(product);

        AtomicInteger failCount = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(10);

        // 10개 스레드 실행
        for (User user : users) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // 모든 스레드가 준비될 때까지 대기

                    // 각 스레드에서 사용자 정보를 사용하여 좋아요 수행
                    SecurityUserDto userDto = SecurityUserDto.builder()
                            .userId(user.getUserId())
                            .userRole(EAuthority.ROLE_USER)
                            .build();
                    System.out.println("userDto.getUserId() = " + userDto.getUserId());
                    String s = productService.likeProduct(userDto, product.getProductId());
                    System.out.println("s = " + s);
                    System.out.println("User " + user.getNickname() + " liked the product successfully.");
                } catch (Exception e) {
                    failCount.incrementAndGet(); // 예외 발생 시 카운트 증가
                    System.err.println("Error while user " + user.getNickname() + " trying to like the product: " + e.getMessage());
                } finally {
                    endLatch.countDown(); // 스레드 완료 시 카운트 감소
                }
            });
        }

        startLatch.countDown(); // 모든 스레드 시작
        endLatch.await(); // 모든 스레드 완료 대기

        // ExecutorService 종료z
        executorService.shutdown();
        // Then
        Product findProduct = productService.findById(product.getProductId());

        // 결과 검증
        System.out.println("findProduct.getLikeCount() = " + findProduct.getLikeCount());
//        Assertions.assertThat(failCount.get()).isEqualTo(0); // 실패한 스레드 수 확인
        Assertions.assertThat(findProduct.getLikeCount()).isEqualTo(10 - failCount.get()); // 좋아요 카운트 확인

    }

}