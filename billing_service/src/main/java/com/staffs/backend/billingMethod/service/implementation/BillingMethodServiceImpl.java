package com.staffs.backend.billingMethod.service.implementation;

import com.staffs.backend.billingMethod.dto.BillingMethodDTO;
import com.staffs.backend.billingMethod.dto.BillingMethodRequestDTO;
import com.staffs.backend.billingMethod.service.BillingMethodService;
import com.staffs.backend.entity.billingMethod.BillingMethod;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.repository.billingMethod.BillingMethodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BillingMethodServiceImpl implements BillingMethodService {

    private final BillingMethodRepository billingMethodRepository;

    public BillingMethodServiceImpl(BillingMethodRepository billingMethodRepository) {
        this.billingMethodRepository = billingMethodRepository;
    }

    @Override
    public BillingMethodDTO saveBillingMethod(BillingMethodRequestDTO dto) {
        log.info("saving billing method info!");

        if (!billingMethodRepository.existsByBillingMethodName(dto.getBillingMethodName())) {
            log.info("saving new billing method => {}" , dto.getBillingMethodName());

            BillingMethod billingMethod = new BillingMethod();
            BeanUtils.copyProperties(dto , billingMethod);
            billingMethod.setCreatedAt(LocalDateTime.now());

            //save to DB
            billingMethod = billingMethodRepository.save(billingMethod);

            return getBillingMethodDTO(billingMethod);

        } else {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
        }

    }

    @Override
    public BillingMethodDTO getBillingMethodByName(String billingMethodName) {
        log.info("getting single billingMethod DTO info");

        BillingMethod billingMethod = billingMethodRepository.findByBillingMethodNameAndValidate(billingMethodName , true).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));

        return getBillingMethodDTO(billingMethod);
    }

    @Override
    public List<BillingMethodDTO> getBillingMethods() {
        log.info("getting billingMethod DTOs info");

        List<BillingMethod> billingMethods = billingMethodRepository.findAll();

        return billingMethods.stream().map(this::getBillingMethodDTO).collect(Collectors.toList());

    }

    @Override
    public BillingMethod getBillingMethodById(Long billingMethodId) {
        return getByBillingMethodId(billingMethodId);
    }

    @Override
    public BillingMethodDTO getBillingMethodDTOById(Long billingMethodId) {
        return getBillingMethodDTO(getByBillingMethodId(billingMethodId));
    }

    @Override
    public void validateBillingMethod(String name , boolean status) {
        log.info("validating billing method");

        BillingMethod billingMethod = billingMethodRepository.findByBillingMethodName(name).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));

        billingMethod.setValidate(status);
        billingMethod.setUpdatedAt(LocalDateTime.now());

        //save to DB
        billingMethodRepository.save(billingMethod);

    }

    private BillingMethod getByBillingMethodId(Long billingMethodId) {
        return billingMethodRepository.findByBillingMethodIDAndValidate(billingMethodId , true).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private BillingMethodDTO getBillingMethodDTO(BillingMethod billingMethod) {
        log.info("converting billingMethod to billingMethod DTO");

        BillingMethodDTO billingMethodDTO = new BillingMethodDTO();
        BeanUtils.copyProperties(billingMethod , billingMethodDTO);

        return billingMethodDTO;

    }

}
