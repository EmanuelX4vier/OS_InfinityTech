package com.infinity.crud.service.auth;

import com.infinity.crud.dto.authdto.AuthTokens;
import com.infinity.crud.dto.authdto.LoginRequestDTO;
import com.infinity.crud.dto.userdto.UserRequestDTO;
import com.infinity.crud.entity.RefreshToken;
import com.infinity.crud.entity.User;
import com.infinity.crud.exception.EmailAlreadyExistsException;
import com.infinity.crud.repository.UserRepository;
import com.infinity.crud.security.JwtService;
import com.infinity.crud.service.refresh.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public void register(UserRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        User user = User.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .funcao(request.getFuncao())
                .build();

        userRepository.save(user);
    }


    public AuthTokens login(LoginRequestDTO request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));


        String accessToken = jwtService.generateToken(user.getEmail());
        refreshTokenService.revokeAllByUser(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthTokens(
                accessToken,
                refreshToken.getToken()
        );
    }

    public AuthTokens refreshToken(String refreshToken) {

        //Valida o refresh token (existência, expiração e revogação)
        RefreshToken token = refreshTokenService.validateToken(refreshToken);

        User user = token.getUser();

        //Gera novo acesso token
        String newAccessToken = jwtService.generateToken(user.getEmail());

        refreshTokenService.revokeToken(refreshToken);
        refreshTokenService.deleteToken(refreshToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthTokens(newAccessToken, newRefreshToken.getToken());
    }

    public void logout(String refreshToken) {

        refreshTokenService.revokeToken(refreshToken);
    }
}