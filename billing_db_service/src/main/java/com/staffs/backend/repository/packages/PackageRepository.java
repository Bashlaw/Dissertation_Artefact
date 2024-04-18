package com.staffs.backend.repository.packages;

import com.staffs.backend.entity.packageType.PackageType;
import com.staffs.backend.entity.packages.Packages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PackageRepository extends JpaRepository<Packages, Long> {

    boolean existsByPackageNameAndPackageType(String packageName, PackageType packageType);

    Optional<Packages> findByPackageNameAndPackageTypeAndActivation(String packageName, PackageType packageType, boolean activation);

    Optional<Packages> findByPackageNameAndActivation(String packageName, boolean activation);

    List<Packages> findByPackageTypeAndActivation(PackageType packageType, boolean activation);

    Optional<Packages> findByPackageNameAndPackageType(String packageName, PackageType packageType);

    @Query(value = "select b.packages_package_id from billing_setup_packages b where b.billing_setup_bill_id = ?1", nativeQuery = true)
    Long getPackageIdForBill(UUID billId);

}
