package com.docdoku.quarkus.tp;

import jakarta.json.bind.annotation.JsonbProperty;

public record Product(
        @JsonbProperty("id") Long id,
        @JsonbProperty("name") String name,
        @JsonbProperty("price") double price,
        @JsonbProperty("category") String category) {
}
