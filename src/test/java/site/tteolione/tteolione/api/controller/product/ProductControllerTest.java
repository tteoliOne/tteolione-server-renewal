package site.tteolione.tteolione.api.controller.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.ControllerTestSupport;
import site.tteolione.tteolione.GenerateMockToken;
import site.tteolione.tteolione.WithMockCustomAccount;
import site.tteolione.tteolione.api.controller.product.request.PostProductReq;
import site.tteolione.tteolione.api.service.product.dto.CategoryProductDto;
import site.tteolione.tteolione.api.service.product.dto.ProductDto;
import site.tteolione.tteolione.api.service.product.request.PostProductServiceReq;
import site.tteolione.tteolione.api.service.product.response.GetSimpleProductRes;
import site.tteolione.tteolione.api.service.product.response.PostProductRes;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.common.util.SecurityUtils;
import site.tteolione.tteolione.domain.product.constants.EProductSoldStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ProductControllerTest extends ControllerTestSupport {

    @DisplayName("상품 등록 통과 테스트")
    @Test
    @WithMockCustomAccount
    void createProduct() throws Exception {
        // given
        PostProductReq request = PostProductReq.builder()
                .categoryId(1L)
                .title("test_title")
                .buyPrice(10000)
                .buyCount(5)
                .sharePrice(4000)
                .shareCount(4)
                .buyDate(LocalDateTime.now())
                .content("test_content")
                .longitude(123.45)
                .latitude(45.123)
                .build();

        MockMultipartFile photo1 = createMultipart("photos", "product1.jpg");
        MockMultipartFile photo2 = createMultipart("photos", "product2.jpg");
        MockMultipartFile photo3 = createMultipart("photos", "product3.jpg");

        MockMultipartFile receipt = createMultipart("receipt", "receipt1.jpg");

        MockMultipartFile createProductRequest = new MockMultipartFile(
                "request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        PostProductRes response = PostProductRes.builder()
                .productId(1L)
                .userId(1L)
                .result("정상적으로 게시되었습니다.")
                .build();

        // when
        Mockito.when(productService.saveProduct(
                        Mockito.any(SecurityUserDto.class),
                        Mockito.anyList(),
                        Mockito.any(MultipartFile.class),
                        Mockito.any(PostProductServiceReq.class)))
                .thenReturn(response);
        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/products")
                        .file(photo1)
                        .file(photo2)
                        .file(photo3)
                        .file(receipt)
                        .file(createProductRequest)
                        .headers(GenerateMockToken.getToken())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.productId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.result").value("정상적으로 게시되었습니다."));
    }

    @DisplayName("상품 등록 실패 테스트 - null 카테고리Id")
    @Test
    @WithMockCustomAccount(loginId = "test123", email = "test123@naver.com", username = "testUser")
    void createProduct_NullCategoryId() throws Exception {
        // given
        SecurityUserDto userDto = SecurityUtils.getUser();

        PostProductReq request = PostProductReq.builder()
                .categoryId(null)
                .title("test_title")
                .buyPrice(10000)
                .buyCount(5)
                .sharePrice(4000)
                .shareCount(4)
                .buyDate(LocalDateTime.now())
                .content("test_content")
                .longitude(123.45)
                .latitude(45.123)
                .build();

        MockMultipartFile photo1 = createMultipart("photos", "product1.jpg");
        MockMultipartFile photo2 = createMultipart("photos", "product2.jpg");
        MockMultipartFile photo3 = createMultipart("photos", "product3.jpg");

        MockMultipartFile receipt = createMultipart("receipt", "receipt1.jpg");

        MockMultipartFile createProductRequest = new MockMultipartFile(
                "request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        PostProductRes response = PostProductRes.builder()
                .productId(1L)
                .userId(1L)
                .result("정상적으로 게시되었습니다.")
                .build();

        // when
        Mockito.when(productService.saveProduct(
                        Mockito.any(SecurityUserDto.class),
                        Mockito.anyList(),
                        Mockito.any(MultipartFile.class),
                        Mockito.any(PostProductServiceReq.class)))
                .thenReturn(response);
        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/products")
                        .file(photo1)
                        .file(photo2)
                        .file(photo3)
                        .file(receipt)
                        .file(createProductRequest)
                        .headers(GenerateMockToken.getToken())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("카테고리 Id를 입력해주세요."));
    }

    @DisplayName("상품 등록 실패 테스트 - 빈문자열 상품 제목")
    @Test
    @WithMockCustomAccount
    void createProduct_EmptyTitle() throws Exception {
        // given
        PostProductReq request = PostProductReq.builder()
                .categoryId(1L)
                .title("")
                .buyPrice(10000)
                .buyCount(5)
                .sharePrice(4000)
                .shareCount(4)
                .buyDate(LocalDateTime.now())
                .content("test_content")
                .longitude(123.45)
                .latitude(45.123)
                .build();

        MockMultipartFile photo1 = createMultipart("photos", "product1.jpg");
        MockMultipartFile photo2 = createMultipart("photos", "product2.jpg");
        MockMultipartFile photo3 = createMultipart("photos", "product3.jpg");

        MockMultipartFile receipt = createMultipart("receipt", "receipt1.jpg");

        MockMultipartFile createProductRequest = new MockMultipartFile(
                "request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        PostProductRes response = PostProductRes.builder()
                .productId(1L)
                .userId(1L)
                .result("정상적으로 게시되었습니다.")
                .build();

        // when
        Mockito.when(productService.saveProduct(
                        Mockito.any(SecurityUserDto.class),
                        Mockito.anyList(),
                        Mockito.any(MultipartFile.class),
                        Mockito.any(PostProductServiceReq.class)))
                .thenReturn(response);
        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/products")
                        .file(photo1)
                        .file(photo2)
                        .file(photo3)
                        .file(receipt)
                        .file(createProductRequest)
                        .headers(GenerateMockToken.getToken())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 제목을 입력해주세요."));
    }

    @DisplayName("상품 등록 실패 테스트 - 양수가 아닌 상품 구매 가격")
    @Test
    @WithMockCustomAccount
    void createProduct_NotPositiveBuyPrice() throws Exception {
        // given
        PostProductReq request = PostProductReq.builder()
                .categoryId(1L)
                .title("test_title")
                .buyPrice(0)
                .buyCount(5)
                .sharePrice(4000)
                .shareCount(4)
                .buyDate(LocalDateTime.now())
                .content("test_content")
                .longitude(123.45)
                .latitude(45.123)
                .build();

        MockMultipartFile photo1 = createMultipart("photos", "product1.jpg");
        MockMultipartFile photo2 = createMultipart("photos", "product2.jpg");
        MockMultipartFile photo3 = createMultipart("photos", "product3.jpg");

        MockMultipartFile receipt = createMultipart("receipt", "receipt1.jpg");

        MockMultipartFile createProductRequest = new MockMultipartFile(
                "request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        PostProductRes response = PostProductRes.builder()
                .productId(1L)
                .userId(1L)
                .result("정상적으로 게시되었습니다.")
                .build();

        // when
        Mockito.when(productService.saveProduct(
                        Mockito.any(SecurityUserDto.class),
                        Mockito.anyList(),
                        Mockito.any(MultipartFile.class),
                        Mockito.any(PostProductServiceReq.class)))
                .thenReturn(response);
        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/products")
                        .file(photo1)
                        .file(photo2)
                        .file(photo3)
                        .file(receipt)
                        .file(createProductRequest)
                        .headers(GenerateMockToken.getToken())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("구입 가격을 입력해주세요."));
    }

    @DisplayName("메인화면 상품 조회 통과 테스트")
    @Test
    @WithMockCustomAccount
    void getSimpleProducts_Success() throws Exception{
        // given
        double longitude = 127.0;
        double latitude = 37.0;

        ProductDto productDto1 = createProductDto(1L, "1.jpg", "테스트제목1", 2000, 500.0, 10, 5, EProductSoldStatus.eNew, true);
        ProductDto productDto2 = createProductDto(2L, "2.jpg", "테스트제목2", 2000, 500.0, 10, 5, EProductSoldStatus.eNew, false);
        ProductDto productDto3 = createProductDto(3L, "3.jpg", "테스트제목3", 2000, 500.0, 10, 5, EProductSoldStatus.eNew, true);

        ProductDto productDto4 = createProductDto(4L, "4.jpg", "테스트제목4", 2000, 500.0, 10, 5, EProductSoldStatus.eNew, true);
        ProductDto productDto5 = createProductDto(5L, "5.jpg", "테스트제목5", 2000, 500.0, 10, 5, EProductSoldStatus.eNew, false);
        ProductDto productDto6 = createProductDto(6L, "6.jpg", "테스트제목6", 2000, 500.0, 10, 5, EProductSoldStatus.eNew, true);

        CategoryProductDto categoryDto1 = CategoryProductDto.builder()
                .categoryId(1L)
                .categoryName("카테고리1")
                .products(List.of(productDto1, productDto2, productDto3))
                .build();

        CategoryProductDto categoryDto2 = CategoryProductDto.builder()
                .categoryId(2L)
                .categoryName("카테고리2")
                .products(List.of(productDto4, productDto5, productDto6))
                .build();

        GetSimpleProductRes response = GetSimpleProductRes.from(List.of(categoryDto1, categoryDto2));
        BDDMockito.when(productService.getSimpleProducts(
                        Mockito.any(SecurityUserDto.class),
                        ArgumentMatchers.eq(longitude),
                        ArgumentMatchers.eq(latitude))
                ).thenReturn(response);

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/products/simple")
                        .param("longitude", String.valueOf(longitude))
                        .param("latitude", String.valueOf(latitude))
                        .headers(GenerateMockToken.getToken())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].categoryId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].categoryName").value("카테고리1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].products").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].products[0].productId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].products[1].productId").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].products[2].productId").value(3L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].categoryId").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].categoryName").value("카테고리2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].products").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].products[0].productId").value(4L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].products[1].productId").value(5L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].products[2].productId").value(6L));
    }

    private MockMultipartFile createMultipart(String name, String originName) {
        MockMultipartFile photo = new MockMultipartFile(
                name,
                originName,
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );
        return photo;
    }

    private ProductDto createProductDto(Long productId, String imageUrl, String title, int unitPrice,
                                        double walkingDistance, int walkingTime, int totalLikes,
                                        EProductSoldStatus soldStatus, boolean liked) {
        return ProductDto.builder()
                .productId(productId)
                .imageUrl(imageUrl)
                .title(title)
                .unitPrice(unitPrice)
                .walkingDistance(walkingDistance)
                .walkingTime(walkingTime)
                .totalLikes(totalLikes)
                .soldStatus(soldStatus)
                .liked(liked)
                .build();
    }


}
