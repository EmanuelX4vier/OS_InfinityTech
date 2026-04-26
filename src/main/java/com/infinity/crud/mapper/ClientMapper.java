package com.infinity.crud.mapper;

import com.infinity.crud.dto.clientdto.ClientRequestDTO;
import com.infinity.crud.dto.clientdto.ClientResponseDTO;
import com.infinity.crud.dto.equip.EquipResponseDTO;
import com.infinity.crud.entity.Client;
import lombok.*;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ClientMapper {

    private final EquipMapper equipMapper;

    public Client toEntity (ClientRequestDTO dto){
        return Client.builder().nome(dto.getNome()).telefone(dto.getTelefone()).endereco(dto.getEndereco()).build();
    }

    public ClientResponseDTO toResponseDTO(Client entity){
        List<EquipResponseDTO> equipsDTO = (entity.getEquips() == null) ? List.of() : entity.getEquips().stream().map(equipMapper::toResponseDTO).toList();
        return new ClientResponseDTO(entity.getId(), entity.getNome(), entity.getTelefone(), entity.getEndereco(), equipsDTO, entity.getDataCadastro());
    }
}
