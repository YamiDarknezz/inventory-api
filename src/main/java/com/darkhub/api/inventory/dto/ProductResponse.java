package com.darkhub.api.inventory.dto;

import com.darkhub.api.inventory.model.Product;

import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Double price,
        Instant createdAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCreatedAt()
        );
    }
}