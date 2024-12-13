package com.wysi.quizigma;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.wysi.quizigma.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtil {

    private final String secret = "mAAfreMWN0HuHNYOd+c759cpS3rxlRjrIsQUsF3x5huXBMKojOgdNojgSCXHhkkq6J6pCYSZEgMJtoXdTVYRh4O1/bwxiHErRxy+QQhslR65VD38pWAqKTfSRJQVrnedgrPZY/ZbJoivaCxaUXnxHqvsFHPlMBlh46bw70MK4nRu5xY3U9ZDzFEUITARYGbvnt6wR/yK4vFhzltU4C/xt+xgrhArJegrv83y1w1BPQlfK/SmlAW8fhSiZm3GN1pyTBfMoXz2cyr+GJy/Z/8s5Q3NO78H5fBrORftgTmZXnaT4AU4Dhfyn/5O8+0gQQI5VWWi5AzdvoLYUXsA0MG5beKN0UCzFD0QzNxBG89TFn4="
;
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
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

    public Integer getUserId(String token) {
        return Integer.parseInt(Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).build().parseClaimsJws(token).getBody().getSubject());
    }
}
