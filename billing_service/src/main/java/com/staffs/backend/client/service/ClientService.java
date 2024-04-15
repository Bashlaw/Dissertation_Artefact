package com.staffs.backend.client.service;

import com.staffs.backend.client.dto.ClientDTO;
import com.staffs.backend.client.dto.ClientListDTO;
import com.staffs.backend.entity.client.Client;
import com.staffs.backend.general.dto.PageableRequestDTO;

public interface ClientService {

    ClientDTO saveClient(ClientDTO dto);

    ClientDTO updateClient(ClientDTO dto);

    ClientDTO getClientDTOByName(String name);

    boolean updateClientActivationStatus(String name, boolean status);

    ClientListDTO getClients(PageableRequestDTO requestDTO);

    Client getClientByName(String name);

}
