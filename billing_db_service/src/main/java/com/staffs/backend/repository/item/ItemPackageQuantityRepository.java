package com.staffs.backend.repository.item;

import com.staffs.backend.entity.item.Item;
import com.staffs.backend.entity.item.ItemPackageQuantity;
import com.staffs.backend.entity.packages.Packages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPackageQuantityRepository extends JpaRepository<ItemPackageQuantity, Long> {

    boolean existsByPackagesAndItem(Packages packages, Item item);

    ItemPackageQuantity findByPackagesAndItem(Packages packages, Item item);

    ItemPackageQuantity findByPackages_PackageNameAndItem_ItemId(String packageName, Long itemId);

}
