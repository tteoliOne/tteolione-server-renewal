package site.tteolione.tteolione.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.tteolione.tteolione.domain.BaseEntity;
import site.tteolione.tteolione.domain.user.constants.EAuthority;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

import java.util.*;

@Entity
@Getter
@Table(name = "USERS")
@NoArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity implements UserDetails {

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

    @Enumerated(EnumType.STRING)
    private EAuthority userRole;

    @Builder
    public User(String loginId, String password, String username, String nickname, String intro, String profile, String targetToken, double ddabongScore, ELoginType loginType, String email, boolean emailAuthChecked, String providerId, boolean activated, EAuthority userRole) {
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
        this.userRole = userRole;
    }

    public void changeTargetToken(String targetToken) {
        this.targetToken = targetToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> userRole.name());
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
