package com.staffs.backend.item.controller;

import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.item.dto.AttachDetachItemDTO;
import com.staffs.backend.item.dto.ItemDTO;
import com.staffs.backend.item.dto.ItemRequestDTO;
import com.staffs.backend.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/item")
public class ItemController {

    private final ItemService itemService;
    private final GeneralService generalService;

    @PostMapping("/add")
    public Response addItem(@Valid @RequestBody ItemRequestDTO requestDTO) {

        return generalService.prepareSuccessResponse(itemService.saveItem(requestDTO));

    }

    @GetMapping("/all/byPackage")
    public Response getItems(@Valid @RequestParam(name = "package name") String packageName) {

        return generalService.prepareSuccessResponse(itemService.getItemDTOsByPackage(packageName));

    }

    @GetMapping("/single")
    public Response getItem(@Valid @RequestParam(name = "item name") String itemName , @Valid @RequestParam(name = "package name") String packageName) {

        return generalService.prepareSuccessResponse(itemService.getItemDTOByNameAndPackage(itemName , packageName));

    }

    @PostMapping("/{itemId}")
    public Response invalidateItem(@PathVariable Long itemId) {

        return generalService.prepareSuccessResponse(itemService.invalidateItem(itemId));

    }

    @PostMapping("/attachDetach")
    public Response attachDetachItemFromPackageWithBody(@Valid @RequestBody AttachDetachItemDTO itemDTO) {

        // attach == 1 && detach == 0
        ItemDTO response = itemService.attachDetachPackage(itemDTO.getType() , itemDTO.getItemId() , itemDTO.getPackageName() , itemDTO.getQuantity());

        return generalService.prepareSuccessResponse(response);

    }

    @GetMapping("/all")
    public Response getAllItems() {

        return generalService.prepareSuccessResponse(itemService.getAllItems());

    }

    @GetMapping("/all/standalone")
    public Response getStandaloneItems() {

        return generalService.prepareSuccessResponse(itemService.getStandaloneItems());

    }

    @GetMapping("/single/standalone")
    public Response getStandaloneItem(@Valid @RequestParam(name = "item name") String itemName) {

        return generalService.prepareSuccessResponse(itemService.getStandaloneItem(itemName));

    }

}
