package com.staffs.backend.billingMethod.service;

import com.staffs.backend.billingMethod.dto.BillingMethodDTO;
import com.staffs.backend.billingMethod.dto.BillingMethodRequestDTO;
import com.staffs.backend.entity.billingMethod.BillingMethod;

import java.util.List;

public interface BillingMethodService {

    BillingMethodDTO saveBillingMethod(BillingMethodRequestDTO dto);

    BillingMethodDTO getBillingMethodByName(String billingMethodName);

    List<BillingMethodDTO> getBillingMethods();

    BillingMethod getBillingMethodById(Long billingMethodId);

    BillingMethodDTO getBillingMethodDTOById(Long billingMethodId);

    void validateBillingMethod(String name, boolean status);

}
