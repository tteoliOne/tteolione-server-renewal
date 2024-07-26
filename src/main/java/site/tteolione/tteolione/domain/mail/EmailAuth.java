package site.tteolione.tteolione.domain.mail;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailAuthId;

    private String email;

    @Builder
    private EmailAuth(String email) {
        this.email = email;
    }

    public static EmailAuth createEmailAuth(String email) {
        return EmailAuth.builder()
                .email(email)
                .build();
    }
}
