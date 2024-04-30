package com.staffs.backend.user.dto;

import com.staffs.backend.general.dto.PageableResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserListDTO extends PageableResponseDTO {

    private List<UserDTO> adminUserList;

}
