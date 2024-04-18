package com.staffs.backend.item.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
public class AttachDetachItemDTO {

    @NotNull
    @NotEmpty(message = "Please provide type")
    private int type;

    @NotNull
    @NotEmpty(message = "Please provide item ID")
    private Long itemId;

    @NotNull
    @NotEmpty(message = "Please provide package name")
    private String packageName;

    @NotNull
    @NotEmpty(message = "Please provide quantity")
    private Long quantity;

}
