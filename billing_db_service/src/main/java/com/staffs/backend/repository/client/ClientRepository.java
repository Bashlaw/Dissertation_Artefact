package com.staffs.backend.repository.client;

import com.staffs.backend.entity.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByClientNameAndOfficeMail(String clientName, String officeMail);

    boolean existsByClientNameAndOfficeMailAndClientIdNot(String clientName, String officeMail, Long clientId);

    Client findByClientNameAndActivation(String clientName, boolean activation);

    Client findByClientName(String clientName);

    List<Client> findByActivation(boolean activation);

}
