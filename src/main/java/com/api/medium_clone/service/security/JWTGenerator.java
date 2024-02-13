package com.api.medium_clone.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTGenerator {

    public String generateToken(Authentication authentication) {
        String username= authentication.getName();
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime()+ SecurityConstants.JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(SecurityConstants.JWT_SECERT.getBytes()))
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(SecurityConstants.JWT_SECERT.getBytes()).build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SecurityConstants.JWT_SECERT.getBytes()).build().parseClaimsJws(token);
            return true;
        }
        catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException("JWT token is not valid " + token);
        }
    }
}