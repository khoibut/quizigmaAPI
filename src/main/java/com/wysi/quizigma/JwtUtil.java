package com.wysi.quizigma;

import java.util.Base64;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.wysi.quizigma.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtil {

    private final String secret = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException | io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.ExpiredJwtException | io.jsonwebtoken.UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).build().parseClaimsJws(token).getBody();
    }
}
