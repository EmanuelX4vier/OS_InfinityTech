package com.infinity.crud.entity;

import com.infinity.crud.enums.Functions;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity //Diz ao banco que isto é uma tabela.
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id //Define que isto é a chave primária.
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Diz ao banco que esse ID deve ser gerado automaticamente.
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Functions funcao;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist //Ele roda antes do hibernate no banco.
    private void registrarCadastro(){
        this.dataCadastro = LocalDateTime.now();
    }
}
