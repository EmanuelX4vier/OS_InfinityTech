package com.infinity.crud.service.refresh;

import com.infinity.crud.entity.RefreshToken;
import com.infinity.crud.entity.User;
import com.infinity.crud.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    //Tempo de duração do token
    @Value("${jwt.refresh-token-expiration-days:7}")
    private long refreshTokenDurationDays;

    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plus(refreshTokenDurationDays, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        return repository.save(refreshToken);
    }

    public RefreshToken validateToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revogado");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expirado");
        }

        return refreshToken;
    }

    public void revokeToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token não encontrado"));

        refreshToken.setRevoked(true);

        repository.save(refreshToken);
    }

    @Transactional
    public void revokeAllByUser(User user) {
        repository.deleteByUser(user);
    }

    @Scheduled(cron = "0 0 3 * * *") // todo dia às 3h da manhã
    public void limparTokensExpirados() {
        repository.deleteByExpiresAtBefore(Instant.now());
        //repository.deleteByRevokedTrue();
    }
}