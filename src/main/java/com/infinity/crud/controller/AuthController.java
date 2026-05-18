package com.infinity.crud.controller;

import com.infinity.crud.dto.authdto.AuthResponseDTO;
import com.infinity.crud.dto.authdto.AuthTokens;
import com.infinity.crud.dto.authdto.LoginRequestDTO;
import com.infinity.crud.dto.userdto.UserRequestDTO;
import com.infinity.crud.service.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // LOGIN

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO request
    ) {

        AuthTokens token = authService.login(request);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 *60)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AuthResponseDTO(token.accessToken()));
    }


    // REGISTER

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody @Valid UserRequestDTO request
    ) {

        authService.register(request);

        return ResponseEntity.ok("Usuário cadastrado com sucesso");
    }


    // REFRESH TOKEN

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(HttpServletRequest request) {

        // 1. pegar refresh token do cookie
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies())
                        .orElse(new Cookie[0]))
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado"));

        // 2. chamar service
        AuthTokens tokens = authService.refreshToken(refreshToken);

        // 3. criar novo cookie (refresh rotation)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        // 4. retorna novo access token no body
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponseDTO(tokens.accessToken()));
    }

    // =========================
    // LOGOUT
    // =========================
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody String refreshToken
    ) {

        authService.logout(refreshToken);

        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}