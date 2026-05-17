package com.infinity.crud.dto.userdto;

import com.infinity.crud.types.Functions;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "O nome do funcionário é obrigatório!")
    private String nome;

    @NotBlank(message = "O email do funcionário é obrigatório!")
    @Email(message = "Email inválido!")
    private String email;

    @NotBlank(message = "A senha do funcionário é obrigatória!")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres!")
    private String senha;

    @NotNull(message = "A função do funcionário é obrigatória!")
    private Functions funcao;
}