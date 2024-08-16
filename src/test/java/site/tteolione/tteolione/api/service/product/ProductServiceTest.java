package site.tteolione.tteolione.api.service.product;

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
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.IntegrationTestSupport;
import site.tteolione.tteolione.WithMockCustomAccount;
import site.tteolione.tteolione.api.service.category.CategoryService;
import site.tteolione.tteolione.api.service.product.request.PostProductServiceReq;
import site.tteolione.tteolione.api.service.product.response.PostProductRes;
import site.tteolione.tteolione.api.service.user.UserService;
import site.tteolione.tteolione.domain.category.Category;
import site.tteolione.tteolione.domain.file.constants.EPhotoType;
import site.tteolione.tteolione.domain.product.Product;
import site.tteolione.tteolione.domain.product.ProductRepository;
import site.tteolione.tteolione.domain.user.User;

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

    @Mock
    private Authentication authentication;


    @AfterEach
    void tearDown() {
    }

    @DisplayName("상품 등록 서비스")
    @Test
    @WithMockCustomAccount(loginId = "test123", email = "test123@naver.com", username = "testUser")
    void saveProduct() {
        //given
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        BDDMockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        BDDMockito.when(authentication.getName()).thenReturn("test123");

        User mockUser = Mockito.mock(User.class);
        BDDMockito.when(mockUser.getUserId()).thenReturn(1L);
        BDDMockito.when(userService.findByLoginId(ArgumentMatchers.anyString())).thenReturn(mockUser);

        Category mockCategory = Mockito.mock(Category.class);
        BDDMockito.when(categoryService.findByCategoryId(ArgumentMatchers.anyLong())).thenReturn(mockCategory);

        Product mockProduct = Mockito.mock(Product.class);
        BDDMockito.when(mockProduct.getUser()).thenReturn(mockUser);
        BDDMockito.when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(mockProduct);

        List<MultipartFile> photos = List.of(Mockito.mock(MultipartFile.class));
        MultipartFile receipt = Mockito.mock(MultipartFile.class);
        PostProductServiceReq request = Mockito.mock(PostProductServiceReq.class);
        BDDMockito.when(request.toEntity(ArgumentMatchers.any(User.class), ArgumentMatchers.any(Category.class))).thenReturn(mockProduct);

        //when
        PostProductRes response = productService.saveProduct(photos, receipt, request);

        //then
        Mockito.verify(userService, Mockito.times(1)).findByLoginId("test123");
        Mockito.verify(categoryService, Mockito.times(1)).findByCategoryId(request.getCategoryId());
        Mockito.verify(productRepository, Mockito.times(1)).save(ArgumentMatchers.any(Product.class));
        Mockito.verify(productFileService, Mockito.times(1)).saveImages(photos, mockProduct, EPhotoType.eProduct);
        Mockito.verify(productFileService, Mockito.times(1)).saveImage(receipt, mockProduct, EPhotoType.eReceipt);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getProductId()).isEqualTo(mockProduct.getProductId());
        Assertions.assertThat(response.getUserId()).isEqualTo(mockProduct.getUser().getUserId());
        Assertions.assertThat(response.getResult()).isEqualTo("정상적으로 게시되었습니다.");
    }

}