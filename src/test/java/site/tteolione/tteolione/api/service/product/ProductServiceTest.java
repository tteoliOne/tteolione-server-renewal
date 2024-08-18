package site.tteolione.tteolione.api.service.product;

import com.querydsl.core.Tuple;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductFileService productFileService;

    @AfterEach
    void tearDown() {
    }

    @DisplayName("상품 등록 서비스")
    @Test
    @WithMockCustomAccount
    void saveProduct() {
        //given
        SecurityUserDto userDto = SecurityUtils.getUser();
        List<MultipartFile> photos = List.of(Mockito.mock(MultipartFile.class));
        MultipartFile receipt = Mockito.mock(MultipartFile.class);

        PostProductServiceReq request = Mockito.mock(PostProductServiceReq.class);
        BDDMockito.when(request.getCategoryId()).thenReturn(1L); // 카테고리 ID 설정

        Category mockCategory = Mockito.mock(Category.class);
        BDDMockito.when(categoryService.findByCategoryId(1L)).thenReturn(mockCategory);

        User mockUser = Mockito.mock(User.class);
        BDDMockito.when(mockUser.getUserId()).thenReturn(1L);
        BDDMockito.when(userService.findById(userDto.getUserId())).thenReturn(mockUser); // Mock 사용자 ID 사용

        Product mockProduct = Product.builder()
                .user(mockUser)
                .category(mockCategory)
                .build();
        ReflectionTestUtils.setField(mockProduct, "productId", 1L);

        BDDMockito.when(request.toEntity(mockUser, mockCategory)).thenReturn(mockProduct);
        BDDMockito.when(productRepository.save(mockProduct)).thenReturn(mockProduct);

        //when
        PostProductRes response = productService.saveProduct(userDto, photos, receipt, request);

        //then
        Mockito.verify(userService, Mockito.times(1)).findById(userDto.getUserId());
        Mockito.verify(categoryService, Mockito.times(1)).findByCategoryId(request.getCategoryId());
        Mockito.verify(productRepository, Mockito.times(1)).save(ArgumentMatchers.any(Product.class));
        Mockito.verify(productFileService, Mockito.times(1)).saveImages(photos, mockProduct, EPhotoType.eProduct);
        Mockito.verify(productFileService, Mockito.times(1)).saveImage(receipt, mockProduct, EPhotoType.eReceipt);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getProductId()).isEqualTo(mockProduct.getProductId());
        Assertions.assertThat(response.getUserId()).isEqualTo(mockProduct.getUser().getUserId());
        Assertions.assertThat(response.getResult()).isEqualTo("정상적으로 게시되었습니다.");
    }

    @DisplayName("상품 메인화면 조회 - 카테고리별로 판매중인 상품 최신순으로 5개 조회(좋아유 유무 포함)")
    @Test
    @WithMockCustomAccount
    void getSimpleProducts() {
        // given
        double userLongitude = 127.0;
        double userLatitude = 37.0;
        SecurityUserDto userDto = SecurityUtils.getUser();

        User mockUser = Mockito.mock(User.class);
        BDDMockito.when(userService.findById(userDto.getUserId())).thenReturn(mockUser);

        Category mockCategory1 = Category.builder()
                .categoryName("카테고리1")
                .build();
        ReflectionTestUtils.setField(mockCategory1, "categoryId", 1L);

        Category mockCategory2 = Category.builder()
                .categoryName("카테고리2")
                .build();
        ReflectionTestUtils.setField(mockCategory2, "categoryId", 1L);

        List<Category> categories = new ArrayList<>();
        categories.add(mockCategory1);
        categories.add(mockCategory2);
        BDDMockito.when(categoryService.findAll()).thenReturn(categories);

        List<Tuple> tuples = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Product mockProduct = Product.builder()
                    .longitude(userLongitude)
                    .latitude(userLatitude)
                    .soldStatus(EProductSoldStatus.eNew)
                    .images(List.of(File.builder().type(EPhotoType.eProduct).fileUrl("1.jpg").build()))
                    .build();
            ReflectionTestUtils.setField(mockProduct, "createdDateTime", LocalDateTime.now().minusMinutes(i));
            ReflectionTestUtils.setField(mockProduct, "productId", (long) i);

            Tuple mockTuple = Mockito.mock(Tuple.class);
            BDDMockito.when(mockTuple.get(QProduct.product)).thenReturn(mockProduct);
            BDDMockito.when(mockTuple.get(QLikes.likes.count())).thenReturn(5L);
            BDDMockito.when(mockTuple.get(QLikes.likes.user.eq(mockUser).as("liked"))).thenReturn(true);

            tuples.add(mockTuple);
        }

        BDDMockito.when(productRepository.findByCategoryAndLikesWithFiles(mockUser, mockCategory1, EProductSoldStatus.eNew, EPhotoType.eProduct)).thenReturn(tuples);
        BDDMockito.when(productRepository.findByCategoryAndLikesWithFiles(mockUser, mockCategory2, EProductSoldStatus.eNew, EPhotoType.eProduct)).thenReturn(tuples);

        // when
        GetSimpleProductRes response = productService.getSimpleProducts(userDto, userLongitude, userLatitude);

        // then
        Mockito.verify(categoryService, Mockito.times(1)).findAll();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getList()).hasSize(2);
        Assertions.assertThat(response.getList().get(0).getProducts()).hasSize(5);
        Assertions.assertThat(response.getList().get(1).getProducts()).hasSize(5);

        for (int i = 0; i < 4; i++) {
            Assertions.assertThat(response.getList().get(0).getProducts().get(i).getSoldStatus()).isEqualTo(EProductSoldStatus.eNew.name());
            Assertions.assertThat(response.getList().get(1).getProducts().get(i).getSoldStatus()).isEqualTo(EProductSoldStatus.eNew.name());
        }
    }
}