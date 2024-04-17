package com.staffs.backend.client.controller;

import com.staffs.backend.client.dto.ClientDTO;
import com.staffs.backend.client.service.ClientService;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/client")
public class ClientController {

    private final ClientService clientService;
    private final GeneralService generalService;

    //testing
    @GetMapping("/test")
    public Response hello() {
        return generalService.prepareSuccessResponse("Billing service up and running :)");
    }

    @PostMapping("/add")
    public Response addClient(@RequestBody ClientDTO clientDTO) {

        return generalService.prepareSuccessResponse(clientService.saveClient(clientDTO));

    }

    @PostMapping("/update")
    public Response updateClient(@RequestBody ClientDTO clientDTO) {

        return generalService.prepareSuccessResponse(clientService.updateClient(clientDTO));

    }

    @PostMapping("/activate")
    public Response activate(@RequestParam(name = "client name") String name , @RequestParam(name = "status") boolean status) {

        return generalService.prepareSuccessResponse(clientService.updateClientActivationStatus(name , status));

    }

    @GetMapping("/all")
    public Response getClients(@Valid @RequestBody PageableRequestDTO requestDTO) {

        return generalService.prepareSuccessResponse(clientService.getClients(requestDTO));

    }

    @GetMapping("/{clientName}")
    public Response getSingleClient(@PathVariable String clientName) {

        ClientDTO response = clientService.getClientDTOByName(clientName);

        return generalService.prepareSuccessResponse(response);

    }

}
