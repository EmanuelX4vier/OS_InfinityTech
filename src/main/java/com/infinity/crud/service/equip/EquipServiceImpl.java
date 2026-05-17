package com.infinity.crud.service.equip;

import com.infinity.crud.dto.equipdto.EquipRequestDTO;
import com.infinity.crud.dto.equipdto.EquipResponseDTO;
import com.infinity.crud.dto.equipdto.EquipUpdateDTO;
import com.infinity.crud.entity.Client;
import com.infinity.crud.entity.Equip;
import com.infinity.crud.exception.ClientNotFoundException;
import com.infinity.crud.exception.EquipNotFoundException;
import com.infinity.crud.mapper.EquipMapper;
import com.infinity.crud.repository.ClientRepository;
import com.infinity.crud.repository.EquipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipServiceImpl implements EquipService {

    private final ClientRepository clientRepository;
    private final EquipRepository equipRepository;
    private final EquipMapper equipMapper;

    @Override
    public EquipResponseDTO createEquip(EquipRequestDTO equipDTO, Long clientId) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(ClientNotFoundException::new);

        Equip equip = equipMapper.toEntity(equipDTO, client);

        Equip savedEquip = equipRepository.save(equip);

        return equipMapper.toResponseDTO(savedEquip);
    }

    @Override
    public EquipResponseDTO searchEquip(String serial) {

        Equip equip = equipRepository.findById(serial)
                .orElseThrow(EquipNotFoundException::new);

        return equipMapper.toResponseDTO(equip);
    }

    @Override
    public EquipResponseDTO updateEquip(String serial, EquipUpdateDTO dto) {

        Equip equip = equipRepository.findById(serial)
                .orElseThrow(EquipNotFoundException::new);

        equip.setDescricao(dto.getDescricao());
        equip.setStatus(dto.getStatus());

        Equip updatedEquip = equipRepository.save(equip);

        return equipMapper.toResponseDTO(updatedEquip);
    }

    @Override
    public void deleteEquip(String serial) {

        if (!equipRepository.existsById(serial)) {
            throw new EquipNotFoundException();
        }

        equipRepository.deleteById(serial);
    }
}