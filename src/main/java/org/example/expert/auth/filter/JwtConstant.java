package org.example.expert.auth.filter;

import lombok.Getter;

@Getter
public enum JwtConstant {
    AUTHORIZATION("Authorization"),
    BEARER("Bearer"),
    ROLE("ROLE_")
    ;

    private final String value;
    JwtConstant(String value) {
        this.value = value;
    }
}
