package com.staffs.backend.item.service;

import com.staffs.backend.entity.item.Item;
import com.staffs.backend.entity.item.ItemPackageQuantity;
import com.staffs.backend.item.dto.ItemDTO;
import com.staffs.backend.item.dto.ItemRequestDTO;

import java.util.List;

public interface ItemService {

    ItemDTO saveItem(ItemRequestDTO requestDTO);

    ItemDTO getItemDTOByNameAndPackage(String itemName , String packageName);

    List<ItemDTO> getItemDTOsByPackage(String packageName);

    List<ItemDTO> getItemDTOsByPackage1(String name);

    Item getItemById(Long itemId);

    boolean invalidateItem(Long itemId);

    ItemDTO getItemDTOById(Long itemId);

    ItemDTO attachDetachPackage(int type , Long itemId , String packageName , Long quantity);

    List<ItemDTO> getAllItems();

    List<ItemDTO> getStandaloneItems();

    ItemDTO getStandaloneItem(String itemName);

    ItemDTO getItemDTOByNameAndPackageName(String itemName , String packageName);

    ItemPackageQuantity getItemPackageQuantity(String packageName , Long itemId);

}
