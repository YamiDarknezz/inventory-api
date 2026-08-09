package com.darkhub.api.inventory.dto;

import jakarta.validation.constraints.*;

public record ProductRequest(
        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @Size(max = 500, message = "description must be at most 500 characters")
        String description,

        @NotNull(message = "price is required")
        @Positive(message = "price must be greater than zero")
        Double price
) {}