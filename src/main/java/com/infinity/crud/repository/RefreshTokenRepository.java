package com.infinity.crud.repository;

import com.infinity.crud.entity.RefreshToken;
import com.infinity.crud.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
    Optional<RefreshToken> findByToken(String token);
    void deleteByExpiresAtBefore(Instant now);
    void deleteByUser(User user);
    //void deleteByRevokedTrue();
}
