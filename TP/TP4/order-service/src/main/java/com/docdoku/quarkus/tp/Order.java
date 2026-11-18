package com.docdoku.quarkus.tp;

public record Order(Long id, String customer, String dish, int quantity) {
}