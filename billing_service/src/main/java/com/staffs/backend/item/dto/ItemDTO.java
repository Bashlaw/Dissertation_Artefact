package com.staffs.backend.item.dto;

import com.staffs.backend.entity.item.Item;
import com.staffs.backend.packages.dto.PackageDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class ItemDTO {

    private Long itemId;

    private String itemName;

    private String Description;

    private String itemRef;

    private double itemCapPrice;

    private String unit;

    private List<PackageDTO> packageDTOs;

    private Long quantity;

    private double itemMinPrice;

    private boolean standalone;

    public static ItemDTO getItemDTO4Package(Item item , String packageName) {

        ItemDTO itemDTO = new ItemDTO();
        BeanUtils.copyProperties(item , itemDTO);
        itemDTO.setItemCapPrice(item.getItemPrice());

        return itemDTO;

    }

}
