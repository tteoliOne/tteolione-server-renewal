package site.tteolione.tteolione.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.tteolione.tteolione.domain.user.User;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private final User user;

    // Constructor
    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Long getId() {
        return user.getUserId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
        return true;
    }
}