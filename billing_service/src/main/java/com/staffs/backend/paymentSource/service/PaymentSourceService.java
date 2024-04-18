package com.staffs.backend.paymentSource.service;

import com.staffs.backend.entity.paymentSource.PaymentSource;
import com.staffs.backend.paymentSource.dto.PaymentSourceDTO;
import com.staffs.backend.paymentSource.dto.PaymentSourceRequestDTO;

import java.util.List;

public interface PaymentSourceService {

    PaymentSourceDTO createSource(PaymentSourceRequestDTO requestDTO);

    PaymentSourceDTO getSingleSourceDTO(String sourceId);

    PaymentSourceDTO getPaymentDTO(PaymentSource paymentSource);

    List<PaymentSourceDTO> getAllSources();

    PaymentSource getSingleSource(String sourceId);

    List<PaymentSourceDTO> getAllSourcesByCountryCode(String code);

}
