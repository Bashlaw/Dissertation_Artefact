package com.staffs.backend.client.dto;

import com.staffs.backend.general.dto.PageableResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientListDTO extends PageableResponseDTO {

    private List<ClientDTO> clients;

}
