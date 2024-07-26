package site.tteolione.tteolione.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import site.tteolione.tteolione.domain.BaseEntity;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority extends BaseEntity {

    @Id
    @Column(name = "authority_name")
    private String authorityName;

    @Builder
    public Authority(String authorityName) {
        this.authorityName = authorityName;
    }
}
