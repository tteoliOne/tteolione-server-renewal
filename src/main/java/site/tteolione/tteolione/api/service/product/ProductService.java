package site.tteolione.tteolione.api.service.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductFileService productFileService;

    @Transactional
    public PostProductRes saveProduct(List<MultipartFile> photos, MultipartFile receipt, PostProductServiceReq request) {
        Category category = categoryService.findByCategoryId(request.getCategoryId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByLoginId(authentication.getName());
        Product saveProduct = productRepository.save(request.toEntity(user, category));

        productFileService.saveImages(photos, saveProduct, EPhotoType.eProduct);
        productFileService.saveImage(receipt, saveProduct, EPhotoType.eReceipt);

        return PostProductRes.from(saveProduct);
    }


}
