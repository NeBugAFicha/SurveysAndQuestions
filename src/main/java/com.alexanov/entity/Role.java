package com.alexanov.entity;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    ADMIN(Set.of("isAdmin")), USER(Set.of("isUser"));
    private Set<String> permissions;
    Role(Set<String> permissions){
        this.permissions = permissions;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities(){
        return getPermissions().stream().map(permission->new SimpleGrantedAuthority(permission)).collect(Collectors.toSet());
    }
}
