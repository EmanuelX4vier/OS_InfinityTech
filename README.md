# 🚀 InfinityTech - CRUD API

Este é o core da API de gerenciamento de ordens de serviço da **InfinityTech**, desenvolvido com foco em alta escalabilidade, manutenibilidade e padrões.

- JDK 21
- Maven 3.8+

O projeto utiliza uma arquitetura modular em camadas, garantindo total separação entre lógica de negócio, persistência e exposição de dados (DTOs).

## 🛠 Tecnologias Utilizadas

- **Java 21**: Versão LTS mais recente para performance e novos recursos.
- **Spring Boot 3.x**: Framework base para a construção da API.
- **Spring Data JPA**: Abstração de persistência e comunicação com banco de dados.
- **Lombok**: Redução de código boilerplate (Getters, Setters, Construtores).
- **Jakarta Validation**: Validação rigorosa de dados de entrada.
- **JWT Token**: Código seguro com Bearer Token. 

## 🏗 Arquitetura do Projeto

A estrutura segue o padrão de **Camadas**:

1.  **Controller**: Gerencia os endpoints e o protocolo HTTP.
2.  **Service**: Camada de regras de negócio (Interfaces e Implementações).
3.  **Repository**: Interface de comunicação com o banco de dados.
4.  **Mapper**: Componentes responsáveis pela conversão entre Entidades e DTOs.
5.  **DTO (Data Transfer Object)**: Segurança e controle total sobre o que é exposto na API.
6.  **Exception Handling**: Tratamento global de erros com respostas padronizadas.
7.  **Security**: Todas as configurações de filtros, validação de token e permissões de rotas.
8.  **Enums**: Separando os tipos enumerados de acordo com a necessidade.

##📡 Endpoints Principais

Autenticação (/auth)

POST /login: Valida usuário no banco.

POST /register: Registra um usuário com a senha em hash.

Usuários (/users)

GET /users/{id}: Busca detalhes de um usuário.

PATCH /users/{id}: Atualização parcial de dados (Safe Update).

Clientes (/clients)
POST /clients: Cadastra um novo cliente parceiro.

GET /clients/{id}: Retorna cliente e sua lista de equipamentos.

Equipamentos (/equips)
POST /equips: Vincula um equipamento a um cliente via Serial Number.

---
📄 Licença
Este projeto é de uso exclusivo e propriedade da InfinityTech.
Desenvolvido por [Emanuel Xavier/Hefesto] ⚡
