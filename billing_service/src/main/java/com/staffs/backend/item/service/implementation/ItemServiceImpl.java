package com.staffs.backend.item.service.implementation;

import com.staffs.backend.entity.item.Item;
import com.staffs.backend.entity.item.ItemPackageQuantity;
import com.staffs.backend.entity.packages.Packages;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.item.dto.ItemDTO;
import com.staffs.backend.item.dto.ItemRequestDTO;
import com.staffs.backend.item.service.ItemService;
import com.staffs.backend.packages.dto.PackageDTO;
import com.staffs.backend.packages.service.PackageService;
import com.staffs.backend.repository.item.ItemPackageQuantityRepository;
import com.staffs.backend.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final PackageService packageService;
    private final ItemPackageQuantityRepository quantityRepository;

    @Override
    public ItemDTO saveItem(ItemRequestDTO requestDTO) {
        log.info("saving item info!");

        Packages packages = null;

        if (itemRepository.existsByItemName(requestDTO.getItemName())) {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
        }

        if (Objects.nonNull(requestDTO.getPackageName()) && !requestDTO.getPackageName().isEmpty()) {
            //get Package
            packages = packageService.getPackageByName(requestDTO.getPackageName());
        }

        if (!itemRepository.existsByItemNameAndPackages_packageNameAndDelFlag(requestDTO.getItemName() , requestDTO.getPackageName() , false)) {
            log.info("Saving new Item => {} {}" , requestDTO.getItemName() , requestDTO.getPackageName());

            Item item = new Item();
            BeanUtils.copyProperties(requestDTO , item);
            item.setPackages(packages);
            item.setCreatedAt(LocalDateTime.now());
            item.setItemPrice(requestDTO.getItemCapPrice());

            //save to DB
            item = itemRepository.save(item);

            if (Objects.nonNull(requestDTO.getPackageName()) && !requestDTO.getPackageName().isEmpty()) {
                return getItemDTO(item , requestDTO.getPackageName());
            } else {
                return getItemDTOWithoutPackageName(item);
            }

        } else {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
        }

    }

    @Override
    public ItemDTO getItemDTOByNameAndPackage(String itemName , String packageName) {
        log.info("getting single Item DTO info");

        return getItemDTO(getSingleItemByName(itemName) , packageName);

    }

    @Override
    public List<ItemDTO> getItemDTOsByPackage(String packageName) {
        log.info("getting Item DTOs info");

        return getItems(packageName).stream().map(item -> getItemDTO(item , packageName)).collect(Collectors.toList());

    }

    @Override
    public List<ItemDTO> getItemDTOsByPackage1(String name) {
        log.info("getting Item DTOs info for packages");

        return getItems(name).stream().map(itemList -> ItemDTO.getItemDTO4Package(itemList , name)).collect(Collectors.toList());

    }

    @Override
    public Item getItemById(Long itemId) {
        return getSingleItemById(itemId);
    }

    @Override
    public boolean invalidateItem(Long itemId) {
        log.info("invalidate Item info!");

        //get Item
        Item item = getSingleItemById(itemId);

        //invalidate
        item.setDelFlag(true);
        item.setUpdatedAt(LocalDateTime.now());

        //save to DB
        itemRepository.save(item);

        return true;
    }

    @Override
    public ItemDTO getItemDTOById(Long itemId) {
        return getItemDTOWithoutPackageName(getSingleItemById(itemId));
    }

    @Override
    public ItemDTO attachDetachPackage(int type , Long itemId , String packageName , Long quantity) {
        log.info("attach/detach item info!");

        //get Package
        Packages packages = packageService.getPackageByName(packageName);

        if (!itemRepository.existsByItemIdAndDelFlag(itemId , false)) {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
        }

        if (itemRepository.existsByItemIdAndPackages_packageNameAndDelFlag(itemId , packageName , false)) {
            log.info("attach/detachItem => {} {}" , itemId , packageName);

            Item item = getItemByIdAndPackageName(itemId , packageName);

            //get existing package list
            List<Packages> packagesList = item.getPackages();

            if (type == 1) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.ITEM_ALREADY_ATTACHED);
            } else if (type == 0) {
                for (Packages packages1 : packagesList) {
                    if (packages1.getPackageId().equals(packages.getPackageId())) {
                        packagesList.remove(packages1);
                        break;
                    }
                }
                item.setPackageList(packagesList);
            }

            item.setUpdatedAt(LocalDateTime.now());

            //save to DB
            item = itemRepository.save(item);

            //set quantity
            if (quantityRepository.existsByPackagesAndItem(packages , item)) {
                log.info("getting existing quantity for the package item");

                ItemPackageQuantity itemPackage = quantityRepository.findByPackagesAndItem(packages , item);

                //set values
                itemPackage.setQuantity(quantity);
                itemPackage.setUpdatedAt(LocalDateTime.now());

                //save to DB
                quantityRepository.save(itemPackage);

            } else {
                //add new quantity
                log.info("adding new quantity for the package item");

                ItemPackageQuantity itemPackage = new ItemPackageQuantity();

                //set values
                itemPackage.setItem(item);
                itemPackage.setPackages(packages);
                itemPackage.setQuantity(quantity);
                itemPackage.setCreatedAt(LocalDateTime.now());

                //save to DB
                quantityRepository.save(itemPackage);

            }
            return getItemDTO4Attach(item , packageName);

        } else {

            Item item = getItemById(itemId);

            //get existing package list
            List<Packages> packagesList = item.getPackages();

            if (type == 1) {
                packagesList.add(packages);
                item.setPackageList(packagesList);
            } else if (type == 0) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.ITEM_CANNOT_BE_DETACHED);
            }

            item.setUpdatedAt(LocalDateTime.now());

            //save to DB
            item = itemRepository.save(item);

            //set quantity
            if (quantityRepository.existsByPackagesAndItem(packages , item)) {
                log.info("getting existing quantity for the existing package item");

                ItemPackageQuantity itemPackage = quantityRepository.findByPackagesAndItem(packages , item);

                //set values
                itemPackage.setQuantity(quantity);
                itemPackage.setUpdatedAt(LocalDateTime.now());

                //save to DB
                quantityRepository.save(itemPackage);

            } else {
                //add new quantity
                log.info("adding new quantity for the existing package item");

                ItemPackageQuantity itemPackage = new ItemPackageQuantity();

                //set values
                itemPackage.setItem(item);
                itemPackage.setPackages(packages);
                itemPackage.setQuantity(quantity);
                itemPackage.setCreatedAt(LocalDateTime.now());

                //save to DB
                quantityRepository.save(itemPackage);

            }
            return getItemDTO4Attach(item , packageName);
        }

    }

    @Override
    public List<ItemDTO> getAllItems() {
        log.info("getting all Item DTOs!");

        return itemRepository.findAll().stream().map(this::getItemDTOWithoutPackageName).collect(Collectors.toList());

    }

    @Override
    public List<ItemDTO> getStandaloneItems() {
        log.info("getting all standalone Items DTOs!");

        return getItems().stream().map(this::getItemDTOWithoutPackageName).collect(Collectors.toList());

    }

    @Override
    public ItemDTO getStandaloneItem(String itemName) {
        log.info("getting single standalone Item DTO!");

        return getItemDTOWithoutPackageName(getSingleStandaloneItemByName(itemName));

    }

    @Override
    public ItemDTO getItemDTOByNameAndPackageName(String itemName , String packageName) {
        log.info("getting single Item DTO info for bill");

        return ItemDTO.getItemDTO4Package(getSingleItemByName(itemName) , packageName);

    }

    @Override
    public ItemPackageQuantity getItemPackageQuantity(String packageName , Long itemId) {
        return quantityRepository.findByPackages_PackageNameAndItem_ItemId(packageName , itemId);
    }

    private Item getItemByIdAndPackageName(Long itemId , String packageName) {
        return itemRepository.findByItemIdAndPackages_packageNameAndDelFlag(itemId , packageName , false)
                .orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private Item getSingleItemById(Long itemId) {
        return itemRepository.findByItemIdAndDelFlag(itemId , false)
                .orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private Item getSingleItemByName(String itemName) {
        log.info("getting single item!");
        return itemRepository.findByItemNameAndDelFlag(itemName , false)
                .orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private Item getSingleStandaloneItemByName(String itemName) {
        log.info("getting single standalone item!");
        return itemRepository.findByItemNameAndDelFlagAndStandalone(itemName , false , true)
                .orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));

    }

    private List<Item> getItems(String packageName) {
        log.info("getting items!");

        return itemRepository.findByPackages_packageNameAndDelFlag(packageName , false);

    }

    private List<Item> getItems() {
        log.info("getting standalone items!");

        return itemRepository.findByDelFlagAndStandalone(false , true);

    }

    private ItemDTO getItemDTO(Item item , String packageName) {
        log.info("converting item to itemDTO with package name");

        ItemDTO itemDTO = new ItemDTO();
        BeanUtils.copyProperties(item , itemDTO);
        itemDTO.setItemCapPrice(item.getItemPrice());

        // get package info
        if (Objects.nonNull(item.getPackages())) {
            List<PackageDTO> packageDTOs = new ArrayList<>();
            PackageDTO packageDTO = packageService.getPackageDTOByName(packageName);
            packageDTOs.add(packageDTO);
            itemDTO.setPackageDTOs(packageDTOs);
        } else {
            itemDTO.setPackageDTOs(null);
        }

        return itemDTO;

    }

    private ItemDTO getItemDTOWithoutPackageName(Item item) {
        log.info("converting item to itemDTO without package name");

        ItemDTO itemDTO = new ItemDTO();
        BeanUtils.copyProperties(item , itemDTO);
        itemDTO.setItemCapPrice(item.getItemPrice());

        return itemDTO;

    }

    private ItemDTO getItemDTO4Attach(Item item , String packageName) {
        log.info("converting item to itemDTO for attach/detach");

        ItemDTO itemDTO = new ItemDTO();
        BeanUtils.copyProperties(item , itemDTO);
        itemDTO.setItemCapPrice(item.getItemPrice());

        // get package info
        if (!item.getPackages().contains(null)) {
            List<PackageDTO> packageDTOs = new ArrayList<>();
            PackageDTO packageDTO = packageService.getPackageDTOByName(packageName);
            packageDTOs.add(packageDTO);
            itemDTO.setPackageDTOs(packageDTOs);

            //set quantity
            itemDTO.setQuantity(quantityRepository.findByPackagesAndItem(packageService.getPackageByName(packageName) , item).getQuantity());

        } else {
            itemDTO.setPackageDTOs(null);
        }

        return itemDTO;
    }

}
