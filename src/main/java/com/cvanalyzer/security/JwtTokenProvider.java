package com.cvanalyzer.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);


    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtIssuer}")
    private String jwtIssuer;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        // Convert secret string to signing key
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Générer un token JWT après authentification
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Extraire le nom d'utilisateur depuis le token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Vérifier que le token est bien formé, signé, non expiré
    public boolean validateToken(String authToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return jwtIssuer.equals(claims.getBody().getIssuer());
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }
}
