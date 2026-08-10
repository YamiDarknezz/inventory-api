package com.darkhub.api.inventory.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceConfigTest {

    @Test
    void constructor_rejectsDefaultSecret() {
        assertThatThrownBy(() -> new JwtService("change-me-in-production", 86400000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("app.jwt.secret");
    }

    @Test
    void constructor_rejectsShortSecret() {
        assertThatThrownBy(() -> new JwtService("too-short", 86400000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 characters");
    }

    @Test
    void constructor_rejectsBlankSecret() {
        assertThatThrownBy(() -> new JwtService("", 86400000))
                .isInstanceOf(IllegalStateException.class);
    }
}