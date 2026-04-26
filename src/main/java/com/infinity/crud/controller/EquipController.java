package com.infinity.crud.controller;

import com.infinity.crud.dto.equip.EquipRequestDTO;
import com.infinity.crud.dto.equip.EquipResponseDTO;
import com.infinity.crud.dto.equip.EquipUpdateDTO;
import com.infinity.crud.service.equip.EquipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/equips")
@RequiredArgsConstructor
public class EquipController {

    private final EquipService equipService;

    @PostMapping
    public ResponseEntity<EquipResponseDTO> createEquip(@RequestBody @Valid EquipRequestDTO dto) {
        Long clientId = dto.getClientId();
        EquipResponseDTO created = equipService.createEquip(dto, clientId);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{serial}")
    public ResponseEntity<EquipResponseDTO> searchEquip(@PathVariable String serial) {

        EquipResponseDTO equip = equipService.searchEquip(serial);
        return ResponseEntity.ok(equip);
    }

    @PatchMapping("/{serial}")
    public ResponseEntity<EquipResponseDTO> updateEquip(@PathVariable String serial,
                                        @RequestBody @Valid EquipUpdateDTO dto) {
       EquipResponseDTO update = equipService.updateEquip(serial, dto);
        return ResponseEntity.ok(update);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{serial}")
    public void deleteEquip(@PathVariable String serial) {
        equipService.deleteEquip(serial);
    }
}