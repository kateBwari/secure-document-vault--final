package org.example._03authservice.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return an empty list or user roles if you have them implemented
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Returns the BCrypt hash
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Returns the username
    }

    // These MUST return true for authentication to succeed
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