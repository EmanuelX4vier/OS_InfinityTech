package com.infinity.crud.dto.authdto;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {}

