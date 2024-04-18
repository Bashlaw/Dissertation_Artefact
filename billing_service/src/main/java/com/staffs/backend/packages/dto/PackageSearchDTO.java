package com.staffs.backend.packages.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PackageSearchDTO {

    @NotNull(message = "Size must be provided, maximum is 100")
    private int size;

    @NotNull(message = "Page must be provided, minimum is 0")
    private int page = 0;

    private String fromDate;

    private String toDate;

}
