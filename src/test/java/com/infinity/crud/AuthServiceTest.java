package com.infinity.crud;

import com.infinity.crud.dto.authdto.AuthResponseDTO;
import com.infinity.crud.dto.authdto.AuthTokens;
import com.infinity.crud.dto.authdto.LoginRequestDTO;
import com.infinity.crud.dto.userdto.UserRequestDTO;
import com.infinity.crud.entity.RefreshToken;
import com.infinity.crud.entity.User;
import com.infinity.crud.enums.Functions;
import com.infinity.crud.repository.UserRepository;
import com.infinity.crud.security.JwtService;
import com.infinity.crud.service.auth.AuthService;
import com.infinity.crud.service.refresh.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    // -------------------------------------------------------------------------
    // register
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("register deve salvar usuário com senha criptografada quando email não existe")
    void register_deveSalvarUsuarioComSenhaCriptografada() {
        UserRequestDTO request = new UserRequestDTO("João", "joao@email.com", "senha123", Functions.TECNICO);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getSenha())).thenReturn("$2a$hashed");

        authService.register(request);

        verify(userRepository, times(1)).save(argThat(user ->
                user.getSenha().equals("$2a$hashed") &&
                user.getEmail().equals("joao@email.com")
        ));
    }

    @Test
    @DisplayName("register deve lançar exceção quando email já existe")
    void register_deveLancarExcecaoParaEmailDuplicado() {
        UserRequestDTO request = new UserRequestDTO("João", "joao@email.com", "senha123", Functions.TECNICO);

        User usuarioExistente = User.builder().email("joao@email.com").build();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuarioExistente));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("Já existe um usuário cadastrado com este email.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register não deve salvar senha em texto puro")
    void register_naoDeveSalvarSenhaEmTextoPuro() {
        UserRequestDTO request = new UserRequestDTO("João", "joao@email.com", "senha123", Functions.TECNICO);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashed");

        authService.register(request);

        verify(passwordEncoder, times(1)).encode("senha123");
    }

    // -------------------------------------------------------------------------
    // login
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("login deve autenticar, gerar tokens e retornar AuthResponseDTO")
    void login_deveDelegarAoAuthenticationManagerERetornarTokens() {
        LoginRequestDTO request = new LoginRequestDTO("joao@email.com", "senha123");

        User userMock = User.builder()
                .email("joao@email.com")
                .senha("encoded")
                .build();

        /*
         * O login agora depende de três colaboradores:
         * 1. authenticationManager — autentica as credenciais
         * 2. jwtService            — gera o access token
         * 3. refreshTokenService   — cria e persiste o refresh token
         * Todos precisam de when() para o teste não quebrar.
         */
        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(userMock));
        when(jwtService.generateToken(anyString()))
                .thenReturn("fake-access-token");

        RefreshToken fakeRefreshToken = RefreshToken.builder()
                .token("fake-refresh-token")
                .user(userMock)
                .build();
        when(refreshTokenService.createRefreshToken(any()))
                .thenReturn(fakeRefreshToken);

        AuthTokens result = authService.login(request);

        // Valida que ambos os tokens estão no retorno
        assertNotNull(result);
        assertEquals("fake-access-token", result.accessToken());
        assertEquals("fake-refresh-token", result.refreshToken());

        // Verifica que a autenticação foi delegada com as credenciais certas
        verify(authenticationManager, times(1)).authenticate(
                argThat(token ->
                        token instanceof UsernamePasswordAuthenticationToken &&
                        token.getPrincipal().equals("joao@email.com") &&
                        token.getCredentials().equals("senha123")
                )
        );
    }

    @Test
    @DisplayName("login deve propagar exceção para credenciais inválidas")
    void login_devePropagrarExcecaoParaCredenciaisInvalidas() {
        LoginRequestDTO request = new LoginRequestDTO("joao@email.com", "senhaErrada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
