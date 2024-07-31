package site.tteolione.tteolione.config;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final String loginId;
    private final String password;
    private final boolean activated;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Long userId;

    public CustomUserDetails(String loginId, String password, boolean activated, Collection<? extends GrantedAuthority> authorities, Long userId) {
        this.loginId = loginId;
        this.password = password;
        this.activated = activated;
        this.authorities = authorities;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }
}

