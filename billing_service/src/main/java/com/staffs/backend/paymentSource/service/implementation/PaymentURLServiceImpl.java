package com.staffs.backend.paymentSource.service.implementation;

import com.staffs.backend.entity.paymentSource.PaymentSource;
import com.staffs.backend.entity.paymentSource.PaymentURL;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.paymentSource.dto.PaymentUrlDTO;
import com.staffs.backend.paymentSource.service.PaymentURLService;
import com.staffs.backend.repository.paymentSource.PaymentUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentURLServiceImpl implements PaymentURLService {

    private final PaymentUrlRepository paymentUrlRepository;

    @Override
    public PaymentUrlDTO savePaymentURL(String url , PaymentSource paymentSource) {
        log.info("saving payment URL info!");

        PaymentURL paymentURL = new PaymentURL();
        paymentURL.setUrl(url);
        paymentURL.setPaymentSources(Collections.singletonList(paymentSource));
        paymentURL.setCreatedAt(LocalDateTime.now());

        //save to DB
        paymentURL = paymentUrlRepository.save(paymentURL);

        return getPaymentUrlDTO(paymentURL);
    }

    @Override
    public List<PaymentUrlDTO> getPaymentUrlsDTOBySource(PaymentSource paymentSource) {
        log.info("getting payment URL DTOs info");

        List<PaymentURL> paymentURLS = getPaymentUrlsBySource(paymentSource.getSourceCode());

        return paymentURLS.stream().map(this::getPaymentUrlDTO).collect(Collectors.toList());
    }

    @Override
    public PaymentURL getPaymentUrlBySource(String url , String paymentSource) {
        return paymentUrlRepository.findByUrlAndPaymentSources_sourceCode(url , paymentSource).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private List<PaymentURL> getPaymentUrlsBySource(String paymentSource) {
        return paymentUrlRepository.findByPaymentSources_sourceCode(paymentSource);
    }

    private PaymentUrlDTO getPaymentUrlDTO(PaymentURL paymentURL) {
        log.info("converting payment url to payment url DTO");

        PaymentUrlDTO paymentUrlDTO = new PaymentUrlDTO();
        BeanUtils.copyProperties(paymentURL , paymentUrlDTO);

        return paymentUrlDTO;

    }

}
