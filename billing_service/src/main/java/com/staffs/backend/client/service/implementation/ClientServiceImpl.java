package com.staffs.backend.client.service.implementation;

import com.staffs.backend.client.dto.ClientDTO;
import com.staffs.backend.client.dto.ClientListDTO;
import com.staffs.backend.client.service.ClientService;
import com.staffs.backend.entity.client.Client;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.repository.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final GeneralService generalService;
    private final ClientRepository clientRepository;

    @Override
    public ClientDTO saveClient(ClientDTO dto) {
        log.info("saving client info!");

        if (!clientRepository.existsByClientNameAndOfficeMail(dto.getClientName() , dto.getOfficeMail())) {
            log.info("saving new client! => {}" , dto.getClientName());

            Client client = new Client();
            BeanUtils.copyProperties(dto , client);
            client.setActivation(true);
            client.setCreatedAt(LocalDateTime.now());

            //save to DB
            client = clientRepository.save(client);

            return getClientDTO(client);

        } else {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
        }

    }

    @Override
    public ClientDTO updateClient(ClientDTO dto) {
        Client client = getClientByName(dto.getClientName());

        if (Objects.nonNull(client)) {

            client.setDescription(dto.getDescription());
            client.setContactPerson(dto.getContactPerson());
            client.setOfficeAddress(dto.getOfficeAddress());
            client.setOfficeMail(dto.getOfficeMail());
            client.setOfficePhoneNo(dto.getOfficePhoneNo());
            client.setUpdatedAt(LocalDateTime.now());

            if (!clientRepository.existsByClientNameAndOfficeMailAndClientIdNot(dto.getClientName() , client.getOfficeMail() , client.getClientId())) {
                log.info("updating client info! => {}" , dto.getClientName());

                clientRepository.save(client);
            } else {
                throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
            }
        } else {
            throw new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND);
        }

        return getClientDTO(client);
    }

    @Override
    public ClientDTO getClientDTOByName(String name) {
        log.info("getting single client");

        return getClientDTO(getClientByName(name));
    }

    @Override
    public Client getClientByName(String name) {
        Client client = clientRepository.findByClientNameAndActivation(name , true);

        if (Objects.nonNull(client)) {
            return client;
        } else {
            throw new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND);
        }
    }

    @Override
    public boolean updateClientActivationStatus(String name , boolean status) {
        log.info("updating client activation status");

        Client client = getClientByNameNoStatus(name);

        if (Objects.nonNull(client)) {
            client.setActivation(status);
            client.setUpdatedAt(LocalDateTime.now());

            clientRepository.save(client);

            return true;
        } else {
            throw new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND);
        }

    }

    @Override
    public ClientListDTO getClients(PageableRequestDTO requestDTO) {
        log.info("getting clients!!!");

        Pageable pageable = generalService.getPageableObject(requestDTO.getSize() , requestDTO.getPage());

        Page<Client> clients = clientRepository.findByActivation(true , pageable);

        return getClientListDTO(clients);
    }

    private Client getClientByNameNoStatus(String name) {
        return clientRepository.findByClientName(name).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private ClientDTO getClientDTO(Client client) {
        log.info("converting client object to clientDTO object!");

        ClientDTO clientDTO = new ClientDTO();
        BeanUtils.copyProperties(client , clientDTO);

        return clientDTO;
    }

    private ClientListDTO getClientListDTO(Page<Client> clientPage) {
        log.info("converting client page to client list dto object!");

        ClientListDTO clientListDTO = new ClientListDTO();

        List<Client> clients = clientPage.getContent();
        if (!clients.isEmpty()) {
            clientListDTO.setHasNextRecord(clientPage.hasNext());
            clientListDTO.setTotalCount((int) clientPage.getTotalElements());
        }

        List<ClientDTO> clientDTOs = clients.stream().map(this::getClientDTO).toList();
        clientListDTO.setClients(clientDTOs);

        return clientListDTO;
    }

}
