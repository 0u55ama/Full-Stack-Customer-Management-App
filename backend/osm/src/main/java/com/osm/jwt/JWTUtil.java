package com.osm.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class JWTUtil {


    private static final String SECRET_KEY = "azerty_123456789_azerty_123456789_azerty_123456789";

    public String issueToken(String subject){
        return issueToken(subject, Map.of());

    }

    public String issueToken(String subject, String ...scopes){
        return issueToken(subject, Map.of("scopes", scopes));

    }
    public String issueToken(
            String subject,
            Map<String, Object> claims) {

        String token = Jwts
                .builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("https://amigoscode.com")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(
                        Date.from(
                                Instant.now().plus(30, DAYS)
                        )
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
