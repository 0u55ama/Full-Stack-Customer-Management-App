package com.osm.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
