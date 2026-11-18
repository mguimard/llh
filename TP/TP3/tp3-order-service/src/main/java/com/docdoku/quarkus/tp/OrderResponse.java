package com.docdoku.quarkus.tp;

import jakarta.json.bind.annotation.JsonbProperty;

public record OrderResponse(
        @JsonbProperty("id") Long id,
        @JsonbProperty("product") Product product,
        @JsonbProperty("quantity") int quantity) {
}
