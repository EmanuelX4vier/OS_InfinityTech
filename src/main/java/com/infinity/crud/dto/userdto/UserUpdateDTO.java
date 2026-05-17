package com.infinity.crud.dto.userdto;

import com.infinity.crud.enums.Functions;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    private String nome;
    private Functions funcao;

}
