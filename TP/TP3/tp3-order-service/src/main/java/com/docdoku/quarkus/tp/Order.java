package com.docdoku.quarkus.tp;

public record Order(Long id, Long productId, int quantity) {
}
