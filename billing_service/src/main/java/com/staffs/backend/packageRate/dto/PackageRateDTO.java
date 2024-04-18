package com.staffs.backend.packageRate.dto;

import com.staffs.backend.entity.packageRate.PackageRate;
import com.staffs.backend.packages.dto.PackageDTO;
import com.staffs.backend.regionRate.dto.RegionRateDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PackageRateDTO {

    private Long versionNo;

    private LocalDateTime effectDate;

    private double rate;

    private boolean validate;

    private PackageDTO packages;

    private List<RegionRateDTO> regionRates;

    public void setRegionRateDTOs(RegionRateDTO regionRateDTO) {
        List<RegionRateDTO> regionRateDTOs = new ArrayList<>();
        regionRateDTOs.add(regionRateDTO);
        setRegionRateDTOList(regionRateDTOs);
    }

    public void setRegionRateDTOList(List<RegionRateDTO> regionRates) {
        this.regionRates = regionRates;
    }

    public static PackageRateDTO getPackageRateDTOForPackages(PackageRate packageRate) {

        PackageRateDTO packageRateDTO = new PackageRateDTO();
        BeanUtils.copyProperties(packageRate , packageRateDTO);

        //get region rate
        //packageRateDTO.setRegionRateDTOList(regionRateService.getAll(packageRate.getPackageRateId()));

        return packageRateDTO;

    }

}
