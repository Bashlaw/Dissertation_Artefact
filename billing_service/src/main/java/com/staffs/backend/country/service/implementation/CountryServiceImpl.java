package com.staffs.backend.country.service.implementation;

import com.staffs.backend.country.dto.CountryDTO;
import com.staffs.backend.country.service.CountryService;
import com.staffs.backend.entity.country.Country;
import com.staffs.backend.entity.paymentSource.PaymentSource;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.paymentSource.dto.PaymentSourceDTO;
import com.staffs.backend.repository.country.CountryRepository;
import com.staffs.backend.repository.paymentSource.PaymentSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final PaymentSourceRepository paymentSourceRepository;

    @Override
    public void updateCountryInfo(String code , PaymentSource paymentSource) {
        log.info("updating country info");

        Country country = getCountryByCode(code.toLowerCase(Locale.ROOT) , 2);

        //country.setSourceList(paymentSource);
        country.setUpdatedAt(LocalDateTime.now());

        //save to DB
        countryRepository.save(country);

    }

    @Override
    public CountryDTO getCountryByCountryCode(Long countryCode) {
        return getCountryDTO(getCountryByCode(countryCode.toString() , 1));
    }

    @Override
    public CountryDTO getCountryDTOByShortCode(String shortCode) {
        return getCountryDTO4PaymentSource(getCountryByCode(shortCode , 2));
    }

    @Override
    public List<CountryDTO> getCountriesDTO(List<Country> countries) {
        return countries.stream().map(this::getCountryDTO4PaymentSource).collect(Collectors.toList());
    }

    @Override
    public Country getCountryByShortCode(String shortCode) {
        return getCountryByCode(shortCode , 2);
    }

    @Override
    public List<CountryDTO> getAllCountriesDTO() {
        return getCountriesDTO(countryRepository.findAll());
    }

    private Country getCountryByCode(String value , int type) {
        //type 1 => countryCode; type 2 => shortCode;

        return switch (type) {
            case 1 ->
                    countryRepository.findByCountryCode(Long.valueOf(value)).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
            case 2 ->
                    countryRepository.findByShortCode(value.toLowerCase(Locale.ROOT)).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
            default ->
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.WRONG_PARAMETER_PARSE);
        };
    }

    private CountryDTO getCountryDTO(Country country) {
        log.info("converting country to country DTO");

        CountryDTO countryDTO = new CountryDTO();
        BeanUtils.copyProperties(country , countryDTO);

        return countryDTO;

    }

    private CountryDTO getCountryDTO4PaymentSource(Country country) {
        log.info("converting country to country DTO for payment source");

        CountryDTO countryDTO = new CountryDTO();
        BeanUtils.copyProperties(country , countryDTO);

        //get paymentSources
        List<PaymentSource> paymentSources = paymentSourceRepository.findByCountryList_shortCode(country.getShortCode());
        List<PaymentSourceDTO> paymentSourceDTOs = paymentSources.stream().map(PaymentSourceDTO::getPaymentSourceDTO4Country).toList();


        countryDTO.setPaymentSources(paymentSourceDTOs);

        return countryDTO;

    }

}
