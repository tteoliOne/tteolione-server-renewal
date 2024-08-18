package site.tteolione.tteolione.docs.product;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.GenerateMockToken;
import site.tteolione.tteolione.WithMockCustomAccount;
import site.tteolione.tteolione.api.controller.product.ProductController;
import site.tteolione.tteolione.api.controller.product.request.PostProductReq;
import site.tteolione.tteolione.api.service.product.ProductService;
import site.tteolione.tteolione.api.service.product.dto.CategoryProductDto;
import site.tteolione.tteolione.api.service.product.dto.ProductDto;
import site.tteolione.tteolione.api.service.product.request.PostProductServiceReq;
import site.tteolione.tteolione.api.service.product.response.GetSimpleProductRes;
import site.tteolione.tteolione.api.service.product.response.PostProductRes;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.docs.RestDocsSupport;
import site.tteolione.tteolione.domain.product.constants.EProductSoldStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class ProductControllerDocsTest extends RestDocsSupport {

    private final ProductService productService = Mockito.mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

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
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("create-product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        ),
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

    @DisplayName("메인화면 - 카테고리별 판매중인 상품 최신순으로 각 5개 조회(좋아요 유무 포함)")
    @Test
    void getSimpleProducts() throws Exception{
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
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("simple-products",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        ),
                        queryParameters(
                                parameterWithName("longitude").description("경도"),
                                parameterWithName("latitude").description("위도")
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
                                fieldWithPath("data.list").type(JsonFieldType.ARRAY)
                                        .description("카테고리 상품 목록"),
                                fieldWithPath("data.list[].categoryId").type(JsonFieldType.NUMBER)
                                        .description("카테고리 ID"),
                                fieldWithPath("data.list[].categoryName").type(JsonFieldType.STRING)
                                        .description("카테고리명"),
                                fieldWithPath("data.list[].products").type(JsonFieldType.ARRAY)
                                        .description("카테고리 상품 목록"),
                                fieldWithPath("data.list[].products[].productId").type(JsonFieldType.NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("data.list[].products[].imageUrl").type(JsonFieldType.STRING)
                                        .description("상품 이미지 url"),
                                fieldWithPath("data.list[].products[].title").type(JsonFieldType.STRING)
                                        .description("상품 제목"),
                                fieldWithPath("data.list[].products[].unitPrice").type(JsonFieldType.NUMBER)
                                        .description("상품 개당 판매 가격"),
                                fieldWithPath("data.list[].products[].walkingDistance").type(JsonFieldType.NUMBER)
                                        .description("상품 거래 도보 거리(m)"),
                                fieldWithPath("data.list[].products[].walkingTime").type(JsonFieldType.NUMBER)
                                        .description("상품 거래 도보 시간(분)"),
                                fieldWithPath("data.list[].products[].totalLikes").type(JsonFieldType.NUMBER)
                                        .description("상품 총 좋아요 갯수"),
                                fieldWithPath("data.list[].products[].soldStatus").type(JsonFieldType.STRING)
                                        .description("상품 판매 상태"),
                                fieldWithPath("data.list[].products[].liked").type(JsonFieldType.BOOLEAN)
                                        .description("로그인 유저 해당 상품 좋아유 유무")
                        )
                ));
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
