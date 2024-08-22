package site.tteolione.tteolione.api.service.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.tteolione.tteolione.domain.likes.Likes;
import site.tteolione.tteolione.domain.likes.LikesRepository;
import site.tteolione.tteolione.domain.product.Product;
import site.tteolione.tteolione.domain.user.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesService {

    private final LikesRepository likesRepository;

    public Likes findByProductAndUser(Product product, User user) {
        Optional<Likes> findLike = likesRepository.findByProductAndUser(product, user);
        return findLike.orElse(null);
    }

    @Transactional
    public void deleteByLike(Likes findLike) {
        likesRepository.delete(findLike);
    }

    @Transactional
    public void createLike(Likes like) {
        likesRepository.save(like);
    }
}
