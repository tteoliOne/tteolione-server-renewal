package site.tteolione.tteolione.domain.user;

import io.lettuce.core.Value;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByEmailAndLoginType(String email, ELoginType eLoginType);

    Optional<User> findByUsernameAndEmail(String username, String email);
}
