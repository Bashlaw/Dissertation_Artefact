package com.staffs.backend.paymentSource.service;

import com.staffs.backend.entity.paymentSource.PaymentSource;
import com.staffs.backend.entity.paymentSource.PaymentURL;
import com.staffs.backend.paymentSource.dto.PaymentUrlDTO;

import java.util.List;

public interface PaymentURLService {

    PaymentUrlDTO savePaymentURL(String url , PaymentSource paymentSource);

    List<PaymentUrlDTO> getPaymentUrlsDTOBySource(PaymentSource paymentSource);

    PaymentURL getPaymentUrlBySource(String url , String paymentSource);

}
