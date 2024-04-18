package com.staffs.backend.repository.item;

import com.staffs.backend.entity.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByItemName(String itemName);

    boolean existsByItemNameAndPackages_packageNameAndDelFlag(String itemName, String packageName, boolean delFlag);

    boolean existsByItemIdAndPackages_packageNameAndDelFlag(Long itemId, String packageName, boolean delFlag);

    boolean existsByItemIdAndDelFlag(Long itemId, boolean delFlag);

    List<Item> findByPackages_packageNameAndDelFlag(String packageName, boolean delFlag);

    List<Item> findByItemNameAndPackages_packageNameAndDelFlag(String itemName, String packageName, boolean delFlag);

    Optional<Item> findByItemIdAndPackages_packageNameAndDelFlag(Long itemId, String packageName, boolean delFlag);

    Optional<Item> findByItemNameAndDelFlag(String itemName, boolean delFlag);

    Optional<Item> findByItemIdAndDelFlag(Long itemId, boolean delFlag);

    List<Item> findByDelFlagAndStandalone(boolean delFlag, boolean standalone);

    Optional<Item> findByItemNameAndDelFlagAndStandalone(String itemName, boolean delFlag, boolean standalone);

}
