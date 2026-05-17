package com.infinity.crud.service.auth;

import com.infinity.crud.dto.authdto.LoginRequestDTO;
import com.infinity.crud.dto.userdto.UserRequestDTO;
import com.infinity.crud.entity.User;
import com.infinity.crud.repository.UserRepository;
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

    public void register(UserRequestDTO request) {

        // Verifica se já existe usuário com o mesmo email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Já existe um usuário cadastrado com este email.");
        }

        // Cria a entidade User com senha criptografada
        User user = User.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .funcao(request.getFuncao())
                .build();

        // Salva no banco
        userRepository.save(user);
    }


     //Executa o login do usuário.

    public Authentication login(LoginRequestDTO request) {

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );
    }
}