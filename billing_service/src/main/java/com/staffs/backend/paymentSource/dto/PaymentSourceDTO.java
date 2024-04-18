package com.staffs.backend.paymentSource.dto;

import com.staffs.backend.country.dto.CountryDTO;
import com.staffs.backend.entity.paymentSource.PaymentSource;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class PaymentSourceDTO {

    private String paymentSourceCode;

    private List<CountryDTO> countryList;

    private List<PaymentUrlDTO> urls;

    public static PaymentSourceDTO getPaymentSourceDTO4Country(PaymentSource paymentSource) {

        PaymentSourceDTO paymentSourceDTO = new PaymentSourceDTO();
        BeanUtils.copyProperties(paymentSource , paymentSourceDTO);
        paymentSourceDTO.setPaymentSourceCode(paymentSource.getSourceCode());

        //get urls DTO
        //paymentSourceDTO.setUrls(paymentURLService.getPaymentUrlsDTOBySource(paymentSource));

        return paymentSourceDTO;

    }

}
