package com.darkhub.api.inventory.exception;

import com.darkhub.api.inventory.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

    @Test
    void notFound_mapsTo404() {
        ResponseEntity<ErrorResponse> res =
                handler.handleNotFound(new NotFoundException("Product not found with id 1"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().status()).isEqualTo(404);
        assertThat(res.getBody().message()).isEqualTo("Product not found with id 1");
        assertThat(res.getBody().path()).isEqualTo("/api/test");
    }

    @Test
    void duplicate_mapsTo409() {
        ResponseEntity<ErrorResponse> res =
                handler.handleDuplicate(new DuplicateException("Username already taken"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(res.getBody().status()).isEqualTo(409);
    }

    @Test
    void auth_mapsTo401() {
        ResponseEntity<ErrorResponse> res =
                handler.handleAuth(new org.springframework.security.authentication.BadCredentialsException("x"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(res.getBody().status()).isEqualTo(401);
        assertThat(res.getBody().message()).isEqualTo("Invalid credentials");
    }

    @Test
    void accessDenied_mapsTo403() {
        ResponseEntity<ErrorResponse> res =
                handler.handleAccessDenied(new org.springframework.security.access.AccessDeniedException("x"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(res.getBody().status()).isEqualTo(403);
    }

    @Test
    void unreadableBody_mapsTo400() {
        ResponseEntity<ErrorResponse> res = handler.handleUnreadable(
                new org.springframework.http.converter.HttpMessageNotReadableException("x"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody().status()).isEqualTo(400);
    }

    @Test
    void generic_mapsTo500_withoutLeakingInternals() {
        ResponseEntity<ErrorResponse> res = handler.handleGeneric(new RuntimeException("boom"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(res.getBody().status()).isEqualTo(500);
        assertThat(res.getBody().message()).isEqualTo("Unexpected server error");
    }
}