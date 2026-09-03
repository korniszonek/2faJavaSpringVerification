package com.example._faEmail.service;

import com.example._faEmail.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(UserModel user){
        /*
        * Token structure:
        *   subject: nickname
        *   issuedAt: date example (12:37 24.08.2026)
        *   claims: user id
        *   expiration date: date example (12:37 24.08.2026) + 24 hrs (expiration duration)(example)
        *   signing key: data structure hashed in Hs256 format with secretkey
        * */
        return Jwts.builder()
                .setSubject(user.getNickname())
                .setIssuedAt(new Date())
                .claim("id", user.getId())
                .setExpiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(getSigningKey())
                .compact();
    }

    public String extractNickname(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public long extractId(String token){
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public boolean isTokenValid(String token, UserModel user) {

        String nickname = extractNickname(token);

        return nickname.equals(user.getNickname())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        ).before(new Date());
    }

}
