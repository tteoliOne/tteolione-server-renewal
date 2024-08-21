package site.tteolione.tteolione.domain.likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.tteolione.tteolione.domain.product.Product;
import site.tteolione.tteolione.domain.user.User;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByProductAndUser(Product product, User user);
}
