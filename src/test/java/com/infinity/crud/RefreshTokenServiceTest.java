package com.infinity.crud;

import com.infinity.crud.entity.RefreshToken;
import com.infinity.crud.entity.User;
import com.infinity.crud.enums.Functions;
import com.infinity.crud.repository.RefreshTokenRepository;
import com.infinity.crud.service.refresh.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;


    @Test
    @DisplayName("createRefreshToken deve salvar token com revoked=false e expiração futura")
    void createRefreshToken_deveSalvarTokenCorreto() {
        User user = buildUser();

        // Captura o objeto RefreshToken que será passado para o repository.save()
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        // Captura o que foi salvo e inspeciona os campos
        verify(repository).save(captor.capture());
        RefreshToken salvo = captor.getValue();

        assertNotNull(salvo.getToken());       // token UUID foi gerado
        assertFalse(salvo.isRevoked());         // começa não revogado
        assertFalse(salvo.getExpiresAt().isAfter(Instant.now())); // expira no futuro
        assertEquals(user, salvo.getUser());   // vinculado ao usuário correto
    }

    @Test
    @DisplayName("createRefreshToken deve gerar tokens únicos a cada chamada")
    void createRefreshToken_deveGerarTokensUnicos() {
        User user = buildUser();
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        RefreshToken token1 = refreshTokenService.createRefreshToken(user);
        RefreshToken token2 = refreshTokenService.createRefreshToken(user);

        assertNotEquals(token1.getToken(), token2.getToken());
    }


    @Test
    @DisplayName("validateToken deve retornar o token quando válido")
    void validateToken_deveRetornarTokenValido() {
        RefreshToken token = buildToken(false, Instant.now().plusSeconds(3600));
        when(repository.findByToken("token-valido")).thenReturn(Optional.of(token));

        RefreshToken result = refreshTokenService.validateToken("token-valido");

        assertNotNull(result);
        assertEquals(token, result);
    }

    @Test
    @DisplayName("validateToken deve lançar exceção quando token não existe")
    void validateToken_deveLancarExcecaoParaTokenNaoEncontrado() {
        when(repository.findByToken("token-inexistente")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> refreshTokenService.validateToken("token-inexistente"));
    }

    @Test
    @DisplayName("validateToken deve lançar exceção quando token está revogado")
    void validateToken_deveLancarExcecaoParaTokenRevogado() {
        RefreshToken token = buildToken(true, Instant.now().plusSeconds(3600));
        when(repository.findByToken("token-revogado")).thenReturn(Optional.of(token));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> refreshTokenService.validateToken("token-revogado"));

        assertTrue(ex.getMessage().contains("revogado"));
    }

    @Test
    @DisplayName("validateToken deve lançar exceção quando token está expirado")
    void validateToken_deveLancarExcecaoParaTokenExpirado() {

        RefreshToken token = buildToken(false, Instant.now().minusSeconds(1));
        when(repository.findByToken("token-expirado")).thenReturn(Optional.of(token));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> refreshTokenService.validateToken("token-expirado"));

        assertTrue(ex.getMessage().contains("expirado"));
    }

    @Test
    @DisplayName("revokeToken deve setar revoked=true e salvar")
    void revokeToken_deveMarcarComoRevogadoESalvar() {
        RefreshToken token = buildToken(false, Instant.now().plusSeconds(3600));
        when(repository.findByToken("token-ativo")).thenReturn(Optional.of(token));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        refreshTokenService.revokeToken("token-ativo");

        // Verifica que revoked foi setado como true antes do save
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repository).save(captor.capture());
        assertTrue(captor.getValue().isRevoked());
    }

    @Test
    @DisplayName("revokeToken deve lançar exceção quando token não existe")
    void revokeToken_deveLancarExcecaoParaTokenNaoEncontrado() {
        when(repository.findByToken("token-inexistente")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> refreshTokenService.revokeToken("token-inexistente"));

        // Garante que o save nunca foi chamado
        verify(repository, never()).save(any());
    }


    private User buildUser() {
        return User.builder()
                .id(1L)
                .nome("João")
                .email("joao@email.com")
                .senha("$2a$hashed")
                .funcao(Functions.TECNICO)
                .build();
    }

    private RefreshToken buildToken(boolean revoked, Instant expiresAt) {
        return RefreshToken.builder()
                .token("token-teste")
                .user(buildUser())
                .revoked(revoked)
                .expiresAt(expiresAt)
                .build();
    }
}
