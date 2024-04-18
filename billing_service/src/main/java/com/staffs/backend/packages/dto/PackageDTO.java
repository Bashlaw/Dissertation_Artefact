package com.staffs.backend.packages.dto;

import com.staffs.backend.item.dto.ItemDTO;
import com.staffs.backend.packageRate.dto.PackageRateDTO;
import com.staffs.backend.packageType.dto.PackageTypeDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PackageDTO {

    private String packageName;

    private String description;

    private Long duration;

    private boolean activation;

    private boolean recurring;

    private PackageTypeDTO packageType;

    private List<ItemDTO> itemList;

    private PackageRateDTO CurrentBillRates;

    private List<PackageRateDTO> packageRates;

    public void setPackageRateDTOs(PackageRateDTO packageRateDTO) {
        List<PackageRateDTO> packageRateDTOs = new ArrayList<>();
        packageRateDTOs.add(packageRateDTO);
        setPackageRateDTOList(packageRateDTOs);
    }

    public void setPackageRateDTOList(List<PackageRateDTO> packageRates) {
        this.packageRates = packageRates;
    }

}
