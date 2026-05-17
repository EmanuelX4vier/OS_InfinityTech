package com.infinity.crud.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Serviço responsável por criar, validar e ler tokens JWT.
 */
@Service
public class JwtService {

    /**
     * Chave secreta vinda do application.properties ou variável de ambiente.
     */
    @Value("${JWT_SECRET}")
    private String secret;

    /**
     * Gera uma chave criptográfica a partir da SECRET
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 1. GERA TOKEN JWT
     * - coloca email dentro do token (subject)
     * - define tempo de expiração
     * - assina com SECRET
     */
    public String generateToken(String email) {

        return Jwts.builder()
                .setSubject(email) // quem é o usuário
                .setIssuedAt(new Date()) // quando foi criado
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // assinatura
                .compact();
    }

    /**
     * 2. EXTRAI O EMAIL DO TOKEN
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * 3. VALIDA TOKEN
     * - verifica assinatura
     * - verifica expiração
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            return extractEmail(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token){
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Lê todas as informações do token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}