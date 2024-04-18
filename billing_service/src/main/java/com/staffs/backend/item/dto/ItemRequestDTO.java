package com.staffs.backend.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ItemRequestDTO {

    @NotEmpty(message = "Item name cannot be empty")
    private String itemName;

    private String Description;

    private String itemRef;

    @PositiveOrZero(message = "Item maximum price must be a positive or zero value")
    private double itemCapPrice;

    @NotEmpty(message = "Unit cannot be empty")
    private String unit;

    @NotEmpty(message = "Package name cannot be empty")
    private String packageName;

    @PositiveOrZero(message = "Item minimum price must be a positive or zero value")
    private double itemMinPrice;

    private boolean standalone;

}
