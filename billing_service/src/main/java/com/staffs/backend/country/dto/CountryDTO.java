package com.staffs.backend.country.dto;

import com.staffs.backend.paymentSource.dto.PaymentSourceDTO;
import lombok.Data;

import java.util.List;

@Data
public class CountryDTO {

    private String shortCode;

    private String countryName;

    private List<PaymentSourceDTO> paymentSources;

}
