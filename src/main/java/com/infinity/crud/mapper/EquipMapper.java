package com.infinity.crud.mapper;

import com.infinity.crud.dto.equipdto.EquipRequestDTO;
import com.infinity.crud.dto.equipdto.EquipResponseDTO;
import com.infinity.crud.entity.Client;
import com.infinity.crud.entity.Equip;
import org.springframework.stereotype.Component;

@Component
public class EquipMapper {

    public Equip toEntity (EquipRequestDTO equipDTO, Client client){

        return Equip.builder().client(client).serial(equipDTO.getSerial()).descricao(equipDTO.getDescricao()).status(equipDTO.getStatus()).build();
    }

    public EquipResponseDTO toResponseDTO(Equip entity){
        return new EquipResponseDTO(entity.getClient().getId(), entity.getSerial(), entity.getDescricao(), entity.getStatus(), entity.getDataCadastro());
    }

}
