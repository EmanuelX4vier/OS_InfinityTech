package com.infinity.crud.mapper;

import com.infinity.crud.dto.userdto.UserRequestDTO;
import com.infinity.crud.dto.userdto.UserResponseDTO;
import com.infinity.crud.entity.User;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Builder
@Component
public class UserMapper {

    public User toEntity (UserRequestDTO dto){
        User user = User.builder().nome(dto.getNome()).funcao(dto.getFuncao()).build();
        return user;
    }

    public UserResponseDTO toResponseDTO(User entity){
        UserResponseDTO userResponseDTO = new UserResponseDTO(entity.getId(), entity.getNome(), entity.getFuncao(), entity.getDataCadastro());
        return userResponseDTO;
    }
}
