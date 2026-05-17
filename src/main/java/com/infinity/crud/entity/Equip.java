package com.infinity.crud.entity;

import com.infinity.crud.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity //Diz ao banco que isto é uma tabela.
@Table(name = "equips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equip {

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @Id
    @Column(nullable = false, unique = true, updatable = false)
    private String serial;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist //Ele roda antes do hibernate no banco.
    private void registrarCadastro(){
        this.dataCadastro = LocalDateTime.now();
    }

}
