package site.tteolione.tteolione.domain.user;

import jakarta.persistence.*;
import lombok.*;
import site.tteolione.tteolione.domain.BaseEntity;
import site.tteolione.tteolione.domain.user.constants.EAuthority;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

import java.util.*;

@Entity
@Getter
@Table(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String loginId;
    private String password;

    private String username;
    private String nickname;
    private String intro;
    private String profile;
    private String targetToken;
    private double ddabongScore;

    @Enumerated(EnumType.STRING)
    private ELoginType loginType;

    private String email;
    private boolean emailAuthChecked;
    private String providerId;
    private boolean activated;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    public static Authority toRoleWithDrawUserAuthority() {
        return Authority.builder()
                .authorityName(EAuthority.eWithdrawalUser.getValue())
                .build();
    }

    public static Authority toRoleDisabledUserAuthority() {
        return Authority.builder()
                .authorityName(EAuthority.eRoleDisabledUser.getValue())
                .build();
    }

    public static Authority toRoleUserAuthority() {
        return Authority.builder()
                .authorityName(EAuthority.eRoleUser.getValue())
                .build();
    }

    @Builder
    public User(String loginId, String password, String username, String nickname, String intro, String profile, String targetToken, double ddabongScore, ELoginType loginType, String email, boolean emailAuthChecked, String providerId, boolean activated, Set<Authority> authorities) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.intro = intro;
        this.profile = profile;
        this.targetToken = targetToken;
        this.ddabongScore = ddabongScore;
        this.loginType = loginType;
        this.email = email;
        this.emailAuthChecked = emailAuthChecked;
        this.providerId = providerId;
        this.activated = activated;
        this.authorities = authorities;
    }
}
