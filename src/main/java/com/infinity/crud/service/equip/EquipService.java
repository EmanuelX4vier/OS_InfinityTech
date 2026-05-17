package com.infinity.crud.service.equip;

import com.infinity.crud.dto.equipdto.EquipRequestDTO;
import com.infinity.crud.dto.equipdto.EquipResponseDTO;
import com.infinity.crud.dto.equipdto.EquipUpdateDTO;

public interface EquipService {
    EquipResponseDTO createEquip(EquipRequestDTO equipRequestDTO, Long clientId);
    EquipResponseDTO searchEquip (String serial);
    EquipResponseDTO updateEquip(String serial, EquipUpdateDTO dto);
    void deleteEquip(String serial);
}
