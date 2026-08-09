package com.darkhub.api.inventory.dto;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {}