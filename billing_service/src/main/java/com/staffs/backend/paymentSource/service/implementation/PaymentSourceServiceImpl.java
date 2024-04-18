package com.staffs.backend.paymentSource.service.implementation;

import com.staffs.backend.country.service.CountryService;
import com.staffs.backend.entity.country.Country;
import com.staffs.backend.entity.paymentSource.PaymentSource;
import com.staffs.backend.entity.paymentSource.PaymentURL;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.paymentSource.dto.PaymentSourceDTO;
import com.staffs.backend.paymentSource.dto.PaymentSourceRequestDTO;
import com.staffs.backend.paymentSource.service.PaymentSourceService;
import com.staffs.backend.paymentSource.service.PaymentURLService;
import com.staffs.backend.repository.paymentSource.PaymentSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSourceServiceImpl implements PaymentSourceService {

    @Lazy
    private final CountryService countryService;
    private final PaymentURLService paymentURLService;
    private final PaymentSourceRepository paymentSourceRepository;

    @Override
    public PaymentSourceDTO createSource(PaymentSourceRequestDTO requestDTO) {
        log.info("create payment source info!");

        if (!paymentSourceRepository.existsBySourceCode(requestDTO.getPaymentSourceCode())) {
            log.info("saving new payment source => {}" , requestDTO.getPaymentSourceCode());

            PaymentSource paymentSource = new PaymentSource();
            BeanUtils.copyProperties(requestDTO , paymentSource);
            paymentSource.setSourceCode(requestDTO.getPaymentSourceCode());
            paymentSource.setCreatedAt(LocalDateTime.now());

            //Save to DB
            paymentSourceRepository.save(paymentSource);

            paymentSource = getSingleSource(requestDTO.getPaymentSourceCode());

            //iterate through countries
            int size = requestDTO.getCountryCodeList().size();

            //List of countries
            List<Country> countries = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                countryService.updateCountryInfo(requestDTO.getCountryCodeList().get(i) , paymentSource);

                //add to the country list
                countries.add(countryService.getCountryByShortCode(requestDTO.getCountryCodeList().get(i)));
            }

            //iterate through the payment source
            int sourceSize = requestDTO.getUrls().size();

            //List of urls
            List<PaymentURL> paymentURLS = new ArrayList<>();

            for (int x = 0; x < sourceSize; x++) {
                paymentURLService.savePaymentURL(requestDTO.getUrls().get(x) , paymentSource);

                //add url to list
                paymentURLS.add(paymentURLService.getPaymentUrlBySource(requestDTO.getUrls().get(x) , paymentSource.getSourceCode()));
            }

            paymentSource.setCountryList(countries);
            paymentSource.setUrlList(paymentURLS);

            //Save to DB
            paymentSource = paymentSourceRepository.save(paymentSource);

            return getPaymentSourceDTO(paymentSource);

        } else {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
        }

    }

    @Override
    public PaymentSourceDTO getSingleSourceDTO(String sourceId) {
        log.info("getting single payment source DTO info by source id");

        return getPaymentSourceDTO(getSingleSource(sourceId));
    }

    @Override
    public PaymentSourceDTO getPaymentDTO(PaymentSource paymentSource) {
        log.info("getting single payment source DTO info by payment source");

        return getPaymentSourceDTO(paymentSource);
    }

    @Override
    public List<PaymentSourceDTO> getAllSources() {
        log.info("getting payment source DTOs info");

        List<PaymentSource> paymentSources = paymentSourceRepository.findAll();

        return paymentSources.stream().map(this::getPaymentSourceDTO).collect(Collectors.toList());
    }

    @Override
    public PaymentSource getSingleSource(String sourceId) {
        log.info("getting single payment source info");

        return paymentSourceRepository.findBySourceCode(sourceId).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    @Override
    public List<PaymentSourceDTO> getAllSourcesByCountryCode(String code) {
        log.info("getting payment source DTOs info by country short code");

        List<PaymentSource> paymentSources = paymentSourceRepository.findByCountryList_shortCode(code);

        return paymentSources.stream().map(PaymentSourceDTO::getPaymentSourceDTO4Country).collect(Collectors.toList());
    }

    private PaymentSourceDTO getPaymentSourceDTO(PaymentSource paymentSource) {
        log.info("converting PaymentSource to PaymentSourceDTO");

        PaymentSourceDTO paymentSourceDTO = new PaymentSourceDTO();
        BeanUtils.copyProperties(paymentSource , paymentSourceDTO);
        paymentSourceDTO.setPaymentSourceCode(paymentSource.getSourceCode());

        //get urls DTO
        paymentSourceDTO.setUrls(paymentURLService.getPaymentUrlsDTOBySource(paymentSource));

        //get countries DTO
        paymentSourceDTO.setCountryList(countryService.getCountriesDTO(paymentSource.getCountryList()));

        return paymentSourceDTO;
    }

}
