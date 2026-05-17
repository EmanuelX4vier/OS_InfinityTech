package com.infinity.crud.dto.authdto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO responsável por receber os dados do login.
 * Apenas transporte de dados (sem regras de negócio).
 */
public record LoginRequestDTO(

        @NotBlank(message = "O email é obrigatório!")
        String email,

        @NotBlank(message = "A senha é obrigatória!")
        String senha
) {}
