package com.infinity.crud.service.client;

import com.infinity.crud.dto.clientdto.ClientRequestDTO;
import com.infinity.crud.dto.clientdto.ClientResponseDTO;
import com.infinity.crud.dto.clientdto.ClientUpdateDTO;
import com.infinity.crud.entity.Client;
import com.infinity.crud.exception.ClientNotFoundException;
import com.infinity.crud.mapper.ClientMapper;
import com.infinity.crud.repository.ClientRepository;
import lombok.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDTO createClient(ClientRequestDTO dto) {

        Client client = clientMapper.toEntity(dto);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponseDTO(savedClient);
    }

    @Override
    public ClientResponseDTO searchClient(Long id) {

        Client client = clientRepository.findById(id).orElseThrow(ClientNotFoundException::new);
        return clientMapper.toResponseDTO(client);
    }

    @Override
    public ClientResponseDTO updateClient(Long id, ClientUpdateDTO dto) {

        Client client = clientRepository.findById(id).orElseThrow(ClientNotFoundException::new);

        if(dto.getNome() != null){
           client.setNome(dto.getNome());
        }
        if(dto.getTelefone() != null){
           client.setTelefone(dto.getTelefone());
        }

        if(dto.getEndereco() != null){
           client.setEndereco(dto.getEndereco());
        }

        Client updateClient = clientRepository.save(client);
        return clientMapper.toResponseDTO(updateClient);
    }

    @Override
    public void deleteClient(Long id) {
        //Verifica se o client existe.
        if (!clientRepository.existsById(id)) {
            throw new ClientNotFoundException();
        }
        clientRepository.deleteById(id);
    }
}