package com.infinity.crud.dto.clientdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.infinity.crud.dto.equipdto.EquipResponseDTO;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponseDTO {

    private Long id;
    private String nome;
    private String telefone;
    private String endereco;
    private List<EquipResponseDTO> equips;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCadastro;

}
