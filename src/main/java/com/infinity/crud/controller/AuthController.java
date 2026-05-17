package com.infinity.crud.controller;

import com.infinity.crud.dto.authdto.LoginRequestDTO;
import com.infinity.crud.dto.userdto.UserRequestDTO;
import com.infinity.crud.security.JwtService;
import com.infinity.crud.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pela autenticação.
 * Aqui fica o login (e futuramente logout, refresh token, etc).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtService jwtService;

    /**
     * Endpoint de login.
     * Recebe email e senha e pede ao Spring Security para autenticar.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO request) {

        /**
         * UsernamePasswordAuthenticationToken é um token interno do Spring,
         * usado apenas durante a autenticação.
         *
         * Ele NÃO é JWT.
         */
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );

        /**
         * Se chegou aqui, significa que:
         * - o usuário existe
         * - a senha está correta
         * - ele foi autenticado
         */

        // Futuramente retornaremos o JWT aqui.
        String token = jwtService.generateToken(request.email());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRequestDTO request) {

        authService.register(request);

        return ResponseEntity.ok("Usuário cadastrado com sucesso");
    }
}