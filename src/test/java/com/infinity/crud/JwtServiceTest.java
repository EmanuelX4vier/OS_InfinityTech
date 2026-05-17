package com.infinity.crud;

import com.infinity.crud.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "a8Fv93KpLm2QzX7nRt5WcV1dJs9HgB6yUi4oP0xZk8eR3mN7tYc5";
    private static final String EMAIL  = "teste@infinitytech.com";

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, SECRET);
    }

    @Test
    @DisplayName("generateToken deve retornar um token não nulo e não vazio")
    void generateToken_deveRetornarTokenValido() {
        String token = jwtService.generateToken(EMAIL);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("generateToken deve gerar tokens diferentes para emails diferentes")
    void generateToken_deveGerarTokensDiferentes() {
        String token1 = jwtService.generateToken("usuario1@email.com");
        String token2 = jwtService.generateToken("usuario2@email.com");
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("extractEmail deve retornar o email que foi colocado no token")
    void extractEmail_deveRetornarEmailCorreto() {
        String token = jwtService.generateToken(EMAIL);
        assertEquals(EMAIL, jwtService.extractEmail(token));
    }

    @Test
    @DisplayName("isTokenValid deve retornar true para token válido com usuário correto")
    void isTokenValid_deveRetornarTrueParaTokenValido() {
        String token = jwtService.generateToken(EMAIL);
        assertTrue(jwtService.isTokenValid(token, buildUserDetails(EMAIL)));
    }

    @Test
    @DisplayName("isTokenValid deve retornar false quando email do token não bate com o usuário")
    void isTokenValid_deveRetornarFalseParaEmailDiferente() {
        String token = jwtService.generateToken("outro@email.com");
        assertFalse(jwtService.isTokenValid(token, buildUserDetails(EMAIL)));
    }

    @Test
    @DisplayName("isTokenValid deve retornar false para token adulterado")
    void isTokenValid_deveRetornarFalseParaTokenAdulterado() {
        String token = jwtService.generateToken(EMAIL);
        String tokenAdulterado = token.substring(0, token.length() - 1) + "X";
        assertFalse(jwtService.isTokenValid(tokenAdulterado, buildUserDetails(EMAIL)));
    }

    @Test
    @DisplayName("isTokenValid deve retornar false para token completamente inválido")
    void isTokenValid_deveRetornarFalseParaTokenLixo() {
        assertFalse(jwtService.isTokenValid("isso.nao.e.um.token", buildUserDetails(EMAIL)));
    }

    private UserDetails buildUserDetails(String email) {
        return User.builder()
                .username(email)
                .password("senhaFake")
                .roles("ADMIN")
                .build();
    }
}
