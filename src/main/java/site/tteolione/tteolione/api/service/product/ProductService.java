package site.tteolione.tteolione.api.service.product;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.api.service.category.CategoryService;
import site.tteolione.tteolione.api.service.likes.LikesService;
import site.tteolione.tteolione.api.service.product.dto.CategoryProductDto;
import site.tteolione.tteolione.api.service.product.dto.ProductDto;
import site.tteolione.tteolione.api.service.product.request.PostProductServiceReq;
import site.tteolione.tteolione.api.service.product.response.GetSimpleProductRes;
import site.tteolione.tteolione.api.service.product.response.PostProductRes;
import site.tteolione.tteolione.api.service.user.UserService;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.common.util.DistanceCalculator;
import site.tteolione.tteolione.domain.category.Category;
import site.tteolione.tteolione.domain.file.constants.EPhotoType;
import site.tteolione.tteolione.domain.likes.Likes;
import site.tteolione.tteolione.domain.likes.QLikes;
import site.tteolione.tteolione.domain.product.Product;
import site.tteolione.tteolione.domain.product.ProductRepository;
import site.tteolione.tteolione.domain.product.QProduct;
import site.tteolione.tteolione.domain.product.constants.EProductSoldStatus;
import site.tteolione.tteolione.domain.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductFileService productFileService;
    private final LikesService likesService;

    @Transactional
    public PostProductRes saveProduct(SecurityUserDto userDto, List<MultipartFile> photos, MultipartFile receipt, PostProductServiceReq request) {
        Category category = categoryService.findByCategoryId(request.getCategoryId());

        User user = userService.findById(userDto.getUserId());

        Product saveProduct = productRepository.save(request.toEntity(user, category));

        productFileService.saveImages(photos, saveProduct, EPhotoType.eProduct);
        productFileService.saveImage(receipt, saveProduct, EPhotoType.eReceipt);
        return PostProductRes.from(saveProduct);
    }


    public GetSimpleProductRes getSimpleProducts(SecurityUserDto userDto, double longitude, double latitude) {
        User user = userService.findById(userDto.getUserId());

        List<Category> categories = categoryService.findAll();

        List<CategoryProductDto> categoryProductDtos = categories.stream()
                .map(category -> {
                    List<Tuple> findResult = productRepository.findByCategoryAndLikesWithFiles(user, category, EProductSoldStatus.eNew, EPhotoType.eProduct);
                    List<ProductDto> productDtos = findResult.stream()
                            .map(tuple -> {
                                Product product = tuple.get(QProduct.product);
                                Long likesCount = tuple.get(QLikes.likes.count());
                                Boolean isLiked = tuple.get(QLikes.likes.user.eq(user).as("liked"));
                                boolean likedValue = (isLiked != null) ? isLiked : false;

                                double walkingDistance = DistanceCalculator.calculateWalkingDistance(product.getLatitude(), product.getLongitude(), latitude, longitude);
                                int walkingTime = DistanceCalculator.calculateWalkingTime(walkingDistance);

                                return createProductDto(product, likesCount, likedValue, walkingDistance, walkingTime);
                            })
                            .collect(Collectors.toList());

                    return CategoryProductDto.builder()
                            .categoryId(category.getCategoryId())
                            .categoryName(category.getCategoryName())
                            .products(productDtos)
                            .build();
                })
                .collect(Collectors.toList());

        return GetSimpleProductRes.from(categoryProductDtos);
    }

    @Transactional
    public String likeProduct(SecurityUserDto userDto, Long productId) {
        Product product = findByIdWithLock(productId);
        if (product == null) {
            throw new GeneralException(Code.NOT_EXISTS_PRODUCT);
        }

        Long userId = userDto.getUserId();
        User user = userService.findById(userId);

        Likes findLike = likesService.findByProductAndUser(product, user);
        if (findLike != null) {
            //좋아요한 적이 있다면 좋아요 취소 처리
            product.removeLike(findLike);
            likesService.deleteByLike(findLike);
            return "상품ID : " + productId + " 좋아요 취소 성공";
        }
        //좋아요한 적이 없다면 좋아요 추가 처리
        Likes like = Likes.builder()
                .user(user)
                .product(product)
                .build();
        likesService.createLike(like);
        product.addLike(like);
        return "상품ID : " + productId + " 좋아요 추가 성공";

    }

    private ProductDto createProductDto(Product product, Long likesCount, boolean likedValue, double walkingDistance, int walkingTime) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .imageUrl(product.getImages().get(0).getFileUrl())
                .unitPrice(product.getSharePrice())
                .totalLikes(likesCount.intValue())
                .soldStatus(product.getSoldStatus())
                .liked(likedValue)
                .walkingDistance(walkingDistance)
                .walkingTime(walkingTime)
                .build();
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new GeneralException(Code.NOT_EXISTS_PRODUCT));
    }

    private Product findByIdWithLock(Long productId) {
        //비관적 락(쓰기) - 데이터에 대한 읽기 및 쓰기를 다른 트랜잭션이 수행할 수 없도록 잠금
        //상품 전체 좋아요 칼럼으로 인한 동시성 제어를 하기 위함
        return productRepository.findByIdWithLock(productId);
    }

}
