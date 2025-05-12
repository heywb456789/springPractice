package com.tomato.naraclub.admin.user.code;

import java.util.Arrays;
import org.springframework.security.core.GrantedAuthority;


public enum AdminRole implements GrantedAuthority {
    SUPER_ADMIN,
    OPERATOR,
    UPLOADER,
    COMMON;

    public static AdminRole of(String role) {
        return Arrays.stream(values())
            .filter(r -> r.name().equals(role))
            .findAny().orElseThrow(() ->
                new IllegalArgumentException("Unknown role: " + role));
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
