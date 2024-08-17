package site.tteolione.tteolione.api.controller.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.api.controller.product.request.PostProductReq;
import site.tteolione.tteolione.api.service.product.response.GetSimpleProductRes;
import site.tteolione.tteolione.api.service.product.ProductService;
import site.tteolione.tteolione.api.service.product.response.PostProductRes;
import site.tteolione.tteolione.common.config.exception.BaseResponse;
import site.tteolione.tteolione.common.util.CurrentUser;
import site.tteolione.tteolione.common.util.SecurityUserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/products")
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록
     */
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<PostProductRes> createProduct(
            @CurrentUser SecurityUserDto userDto,
            @RequestPart(value = "photos") List<MultipartFile> photos,
            @RequestPart(value = "receipt") MultipartFile receipt,
            @Valid @RequestPart(value = "request") PostProductReq request) {

        PostProductRes postProductResponse = productService.saveProduct(userDto, photos, receipt, request.toServiceRequest());
        return BaseResponse.of(postProductResponse);
    }

    @GetMapping("/simple")
    public BaseResponse<GetSimpleProductRes> getSimpleProducts(
            @CurrentUser SecurityUserDto userDto,
            @RequestParam("longitude") double longitude,
            @RequestParam("latitude") double latitude
    ) {
        GetSimpleProductRes simpleProductRes = productService.getSimpleProducts(userDto, longitude, latitude);
        return BaseResponse.of(simpleProductRes);
    }
}
