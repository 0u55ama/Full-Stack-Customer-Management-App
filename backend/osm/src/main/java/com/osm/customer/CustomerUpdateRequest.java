package com.osm.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
