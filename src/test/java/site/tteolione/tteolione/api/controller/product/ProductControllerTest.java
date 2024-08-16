package site.tteolione.tteolione.api.controller.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.ControllerTestSupport;
import site.tteolione.tteolione.WithMockCustomAccount;
import site.tteolione.tteolione.api.controller.product.request.PostProductReq;
import site.tteolione.tteolione.api.service.product.request.PostProductServiceReq;
import site.tteolione.tteolione.api.service.product.response.PostProductRes;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.domain.user.User;

import java.time.LocalDateTime;

public class ProductControllerTest extends ControllerTestSupport {

    @DisplayName("상품 등록 통과 테스트")
    @Test
    @WithMockCustomAccount(loginId = "test123", email = "test123@naver.com", username = "testUser")
    void createProduct() throws Exception {
        // given
        setAuth();

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
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
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
        setAuth();

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
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("카테고리 Id를 입력해주세요."));
    }

    @DisplayName("상품 등록 실패 테스트 - 빈문자열 상품 제목")
    @Test
    @WithMockCustomAccount(loginId = "test123", email = "test123@naver.com", username = "testUser")
    void createProduct_EmptyTitle() throws Exception {
        // given
        setAuth();

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
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 제목을 입력해주세요."));
    }

    @DisplayName("상품 등록 실패 테스트 - 양수가 아닌 상품 구매 가격")
    @Test
    @WithMockCustomAccount(loginId = "test123", email = "test123@naver.com", username = "testUser")
    void createProduct_NotPositiveBuyPrice() throws Exception {
        // given
        setAuth();

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
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("구입 가격을 입력해주세요."));
    }

    private void setAuth() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        BDDMockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        BDDMockito.when(authentication.getName()).thenReturn("test123");

        User mockUser = Mockito.mock(User.class);
        BDDMockito.when(mockUser.getUserId()).thenReturn(1L);
        BDDMockito.when(userService.findByLoginId(ArgumentMatchers.anyString())).thenReturn(mockUser);
    }

    private static MockMultipartFile createMultipart(String name, String originName) {
        MockMultipartFile photo = new MockMultipartFile(
                name,
                originName,
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );
        return photo;
    }


}
