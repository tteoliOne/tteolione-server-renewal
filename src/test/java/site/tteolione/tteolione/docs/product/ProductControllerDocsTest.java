package site.tteolione.tteolione.docs.product;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.WithMockCustomAccount;
import site.tteolione.tteolione.api.controller.product.ProductController;
import site.tteolione.tteolione.api.controller.product.request.PostProductReq;
import site.tteolione.tteolione.api.service.product.ProductService;
import site.tteolione.tteolione.api.service.product.request.PostProductServiceReq;
import site.tteolione.tteolione.api.service.product.response.PostProductRes;
import site.tteolione.tteolione.docs.RestDocsSupport;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;

public class ProductControllerDocsTest extends RestDocsSupport {

    private final ProductService productService = Mockito.mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @DisplayName("상품 등록 통과 테스트")
    @Test
    @WithMockCustomAccount(loginId = "test123", email = "test123@naver.com", username = "testUser")
    void createProduct() throws Exception {
        // given
//        setAuth();

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

        objectMapper.registerModule(new JavaTimeModule());
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
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("create-product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("request").description("상품 등록 요청 정보"),
                                partWithName("photos").description("상품 이미지 파일"),
                                partWithName("receipt").description("영수증 이미지 파일")
                        ),
                        requestPartFields("request",
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER)
                                        .description("카테고리 ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING)
                                        .description("상품 제목"),
                                fieldWithPath("buyPrice").type(JsonFieldType.NUMBER)
                                        .description("상품 구매 가격"),
                                fieldWithPath("buyCount").type(JsonFieldType.NUMBER)
                                        .description("상품 구매 갯수"),
                                fieldWithPath("sharePrice").type(JsonFieldType.NUMBER)
                                        .description("상품 공유 가격"),
                                fieldWithPath("shareCount").type(JsonFieldType.NUMBER)
                                        .description("상품 공유 갯수"),
                                fieldWithPath("buyDate").type(JsonFieldType.ARRAY)
                                        .description("상품 구매 일자"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("상품 내용"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER)
                                        .description("상품 거래 경도"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER)
                                        .description("상품 거래 위도")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공유무"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드값"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("데이터"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .description("사용자 ID"),
                                fieldWithPath("data.productId").type(JsonFieldType.NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("data.result").type(JsonFieldType.STRING)
                                        .description("결과 메시지")
                        )

                ));
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
