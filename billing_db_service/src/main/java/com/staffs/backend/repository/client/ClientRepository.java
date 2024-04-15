package com.staffs.backend.repository.client;

import com.staffs.backend.entity.client.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByClientNameAndOfficeMail(String clientName, String officeMail);

    boolean existsByClientNameAndOfficeMailAndClientIdNot(String clientName, String officeMail, Long clientId);

    Client findByClientNameAndActivation(String clientName, boolean activation);

    Optional<Client> findByClientName(String clientName);

    Page<Client> findByActivation(boolean activation, Pageable pageable);

}
