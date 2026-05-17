package com.infinity.crud.dto.authdto;

/**
 * DTO responsável por receber os dados do login.
 * Apenas transporte de dados (sem regras de negócio).
 */
public record LoginRequestDTO(
        String email,
        String senha
) {}
