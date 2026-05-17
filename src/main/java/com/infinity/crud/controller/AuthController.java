package com.infinity.crud.controller;

import com.infinity.crud.dto.authdto.LoginRequestDTO;
import com.infinity.crud.dto.userdto.UserRequestDTO;
import com.infinity.crud.security.JwtService;
import com.infinity.crud.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pela autenticação.
 * Aqui fica o login (e futuramente logout, refresh token, etc).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    /**
     * Endpoint de login.
     * Recebe email e senha e pede ao Spring Security para autenticar.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDTO request) {

        authService.login(request);

        /**
         * Se chegou aqui, significa que:
         * - o usuário existe
         * - a senha está correta
         * - ele foi autenticado
         */

        String token = jwtService.generateToken(request.email());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRequestDTO request) {

        authService.register(request);

        return ResponseEntity.ok("Usuário cadastrado com sucesso");
    }
}