package com.staffs.backend.paymentIntegration.service.implementation;

import com.staffs.backend.entity.billLog.BillLog;
import com.staffs.backend.entity.billingSetup.model.BillingSetup;
import com.staffs.backend.entity.licenseUpgrade.LicenseUpgrade;
import com.staffs.backend.entity.paymentIntegration.ConfirmPayment;
import com.staffs.backend.entity.paymentIntegration.PaymentIntegration;
import com.staffs.backend.entity.paymentIntegration.PaymentResponse;
import com.staffs.backend.entity.paymentSource.PaymentSource;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.packages.dto.PackageDTO;
import com.staffs.backend.packages.service.PackageService;
import com.staffs.backend.paymentIntegration.dto.*;
import com.staffs.backend.paymentIntegration.service.PaymentIntegrationService;
import com.staffs.backend.paymentIntegration.service.PaymentService;
import com.staffs.backend.paymentSource.service.PaymentSourceService;
import com.staffs.backend.repository.billLog.BillLogRepository;
import com.staffs.backend.repository.billingSetup.BillingSetupRepository;
import com.staffs.backend.repository.licenseUpgrade.LicenseUpgradeRepository;
import com.staffs.backend.repository.paymentIntegration.ConfirmPaymentRepository;
import com.staffs.backend.repository.paymentIntegration.PaymentIntegrationRepository;
import com.staffs.backend.repository.paymentIntegration.PaymentResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PackageService packageService;
    private final BillLogRepository billLogRepository;
    private final PaymentSourceService paymentSourceService;
    private final BillingSetupRepository billingSetupRepository;
    private final ConfirmPaymentRepository confirmPaymentRepository;
    private final LicenseUpgradeRepository licenseUpgradeRepository;
    private final PaymentResponseRepository paymentResponseRepository;
    private final PaymentIntegrationService paymentIntegrationService;
    private final PaymentIntegrationRepository paymentIntegrationRepository;

    @Override
    public PaymentResponseDTO pushPayment(PaymentRequestDTO requestDTO) {
        log.info("push payment and log");

        //build request for different payment sources
        switch (requestDTO.getPaymentSource()) {
            case "DPO":
                if (!containsGeneric(requestDTO)) {
                    requestDTO = paymentIntegrationService.generatePaymentRequestDPO(requestDTO);
                } else {
                    requestDTO = paymentIntegrationService.generatePaymentRequestGeneric(requestDTO);
                }
                break;
            case "MPESA":
                requestDTO = paymentIntegrationService.generatePaymentRequestMPESA(requestDTO);
                break;
            case "PAYSTACK":
                requestDTO = paymentIntegrationService.generatePaymentRequestPAYSTACK(requestDTO);
                break;
        }

        //call integration
        PaymentResponseDTO responseDTO = paymentIntegrationService.getPaymentResponse(requestDTO);

        if (Objects.equals(responseDTO.getMessage() , "Transaction created") || Objects.equals(responseDTO.getMessage() , "STK Initiated Successfully") || Objects.equals(responseDTO.getMessage() , "Authorization URL created")) {

            //save payment request to DB
            PaymentIntegration paymentIntegration = new PaymentIntegration();
            BeanUtils.copyProperties(requestDTO , paymentIntegration);

            //get payment source
            PaymentSource paymentSource = paymentSourceService.getSingleSource(requestDTO.getPaymentSource());

            //set UUID
            String tranRef = UUID.randomUUID().toString();

            //set value
            paymentIntegration.setPaymentId(tranRef);
            paymentIntegration.setAccountId(requestDTO.getAccountId());
            paymentIntegration.setPaymentSource(paymentSource);

            if (requestDTO.getPaymentSource().equals("MPESA")) {
                paymentIntegration.setPhoneNumber(requestDTO.getPhoneNumber());
                paymentIntegration.setTransRef(requestDTO.getTransactionRef());
            }

            paymentIntegrationRepository.save(paymentIntegration);

            //log payment response
            logResponse(responseDTO , paymentIntegration);

            //display transRef
            responseDTO.setTransactionRef(requestDTO.getTransRef());

            return responseDTO;

        } else {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PAYMENT_REQUEST);
        }
    }

    @Override
    public ConfirmResponseDTO confirmPayment(ConfirmRequestDTO requestDTO) {

        //build request for different payment sources
        switch (requestDTO.getPaymentSource()) {
            case "DPO":
                if (!containsGenericConfirmation(requestDTO)) {
                    requestDTO = paymentIntegrationService.generateConfirmRequestDPO(requestDTO);
                } else {
                    requestDTO = paymentIntegrationService.generateConfirmRequestGeneric(requestDTO);
                }
                break;
            case "MPESA":
                requestDTO = paymentIntegrationService.generateConfirmRequestMPESA(requestDTO);
                break;
            case "PAYSTACK":
                requestDTO = paymentIntegrationService.generateConfirmRequestPAYSTACK(requestDTO);
                break;
        }

        //call integration
        ConfirmResponseDTO confirmResponseDTO = paymentIntegrationService.getConfirmPaymentResponse(requestDTO);
        if (requestDTO.getPaymentSource().equals("DPO") || requestDTO.getPaymentSource().equals("PAYSTACK")) {
            String level = confirmResponseDTO.getFraud_level();
            if ((Objects.equals(confirmResponseDTO.getMessage() , "Transaction Paid") && (Objects.equals(level , "001") || Objects.equals(level , "003") || Objects.equals(level , "005"))) || (Objects.equals(confirmResponseDTO.getMessage() , "success") || confirmResponseDTO.getStatus() == 200L)) {

                //set amount
                double amount;
                if (confirmResponseDTO.getAmount().contains(",")) {
                    amount = Double.parseDouble(confirmResponseDTO.getAmount().replace("," , ""));
                    amount = amount / 100;
                } else {
                    amount = Double.parseDouble(confirmResponseDTO.getAmount());
                }

                //update bill setup
                this.updatePaymentStatusOnBillSetup(requestDTO.getAccountId() , requestDTO.getTransRef() , amount);

                //update bill log
                this.updatePaymentStatusOnBillLog(requestDTO.getAccountId() , requestDTO.getTransRef() , amount);

                //save to DB
                if (!confirmPaymentRepository.existsByTransactionRef(requestDTO.getTransRef())) {
                    ConfirmPayment confirmPayment = new ConfirmPayment();
                    BeanUtils.copyProperties(confirmResponseDTO , confirmPayment);

                    //set values
                    confirmPayment.setAmount(String.valueOf(amount));
                    confirmPayment.setConfirmPaymentId(UUID.randomUUID().toString());
                    confirmPayment.setTransactionRef(confirmResponseDTO.getCompany_ref());
                    confirmPayment.setFraudLevel(confirmResponseDTO.getFraud_level());
                    confirmPayment.setCompanyRef(confirmResponseDTO.getTransaction_ref());
                    confirmPayment.setFraudExplanation(confirmResponseDTO.getFraud_explanation());
                    confirmPayment.setTransFee(confirmResponseDTO.getTrans_fee());

                    confirmPaymentRepository.save(confirmPayment);
                }

            } else {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PAYMENT_REQUEST);
            }
        } else if (requestDTO.getPaymentSource().equals("MPESA")) {
            if (confirmResponseDTO.getStatus() == 200L && Objects.equals(confirmResponseDTO.getTransactionStatus() , "Completed")) {

                //update bill setup
                this.updatePaymentStatusOnBillSetup(requestDTO.getAccountId() , requestDTO.getTransRef() , Double.parseDouble(confirmResponseDTO.getAmount()));

                //update bill log
                this.updatePaymentStatusOnBillLog(requestDTO.getAccountId() , requestDTO.getTransRef() , Double.parseDouble(confirmResponseDTO.getAmount()));

                //save to DB
                if (!confirmPaymentRepository.existsByTransactionRef(requestDTO.getTransRef())) {
                    ConfirmPayment confirmPayment = new ConfirmPayment();
                    BeanUtils.copyProperties(confirmResponseDTO , confirmPayment);

                    //set values
                    confirmPayment.setConfirmPaymentId(UUID.randomUUID().toString());
                    confirmPayment.setTransactionRef(confirmResponseDTO.getTransactionData().getMpesaRef());
                    confirmPayment.setFraudLevel(null);
                    confirmPayment.setCompanyRef(confirmResponseDTO.getTransactionReferenceNumber());
                    confirmPayment.setFraudExplanation(null);
                    confirmPayment.setTransFee(confirmResponseDTO.getTransactionData().getAmount());

                    confirmPaymentRepository.save(confirmPayment);
                }

            } else if (confirmResponseDTO.getStatus() == 402L && Objects.equals(confirmResponseDTO.getTransactionStatus() , "Completed")) {
                log.info("return response from mpesa");

                //set values
            } else {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PAYMENT_REQUEST);
            }
        }

        return confirmResponseDTO;

    }

    @Override
    public TransactionResponseDTO cancelTransaction(CancelRequestDTO requestDTO) {

        //call integration
        TransactionResponseDTO responseDTO = paymentIntegrationService.getCancelTransactionResponse(requestDTO);

        //check response
        if (responseDTO.getStatus() != 200L) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PAYMENT_REQUEST);
        }

        return responseDTO;
    }

    @Override
    public TransactionResponseDTO refundTransaction(RefundRequestDTO requestDTO) {

        //call integration
        TransactionResponseDTO responseDTO = paymentIntegrationService.getRefundTransactionResponse(requestDTO);

        //check response
        if (responseDTO.getStatus() != 200L) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PAYMENT_REQUEST);
        }

        return responseDTO;
    }

    private void logResponse(PaymentResponseDTO responseDTO , PaymentIntegration paymentIntegration) {
        log.info("log payment response");

        //save payment response to DB
        PaymentResponse paymentResponse = new PaymentResponse();
        BeanUtils.copyProperties(responseDTO , paymentResponse);

        //set value
        paymentResponse.setPaymentResponseId(UUID.randomUUID().toString());
        paymentResponse.setPaymentIntegration(paymentIntegration);

        paymentResponseRepository.save(paymentResponse);

    }

    private boolean containsGeneric(PaymentRequestDTO paymentRequestDTO) {

        //check if payment request DTO contains generic parameters
        return paymentRequestDTO.getAccountId() != null && paymentRequestDTO.getPaymentType() != null
                && paymentRequestDTO.getClient() != null && paymentRequestDTO.getCountry() != null
                && paymentRequestDTO.getCurrency() != null && paymentRequestDTO.getAmount() != null
                && paymentRequestDTO.getDescription() != null && paymentRequestDTO.getTransRef() != null
                && paymentRequestDTO.getBillReference() != null && paymentRequestDTO.getBackUrl() != null
                && paymentRequestDTO.getRedirectUrl() != null;

    }

    private boolean containsGenericConfirmation(ConfirmRequestDTO dto) {

        //check if confirm payment request DTO contains generic parameters
        return dto.getPlatform() != null && dto.getTransactionReference() != null
                && dto.getPaymentSource() != null;

    }

    private void updatePaymentStatusOnBillLog(String accountId , String paymentRef , double amount) {
        log.info("update payment status on bill log");

        BillLog billLog = billLogRepository.findByAccountIdAndTransRef(accountId , paymentRef).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));

        if (Math.round(billLog.getChargeAmount()) == Math.round(amount)) {
            billLog.setPaymentStatus("Success");
            billLog.setUpdatedAt(LocalDateTime.now());
            billLog.setPaymentSuccess(true);

            //save to DB
            billLog = billLogRepository.save(billLog);

            //save to DB
            billLogRepository.save(billLog);

        } else {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.CHARGE_AMOUNT_IS_NOT_SAME_AS_PAID_AMOUNT);
        }

    }

    private void updatePaymentStatusOnBillSetup(String accountId , String paymentRef , double amount) {
        log.info("update payment status on billing setup");

        BillingSetup billingSetup = billingSetupRepository.findByAccountIdAndValidateAndPaymentSuccessAndPaymentRef(accountId , false , false , paymentRef);

        if (Objects.nonNull(billingSetup)) {
            if (Math.round(billingSetup.getChargeAmount()) == Math.round(amount)) {
                billingSetup.setValidate(true);
                billingSetup.setPaymentSuccess(true);
                billingSetup.setUpdatedAt(LocalDateTime.now());

                if (billingSetup.isLicenseUpgrade()) {
                    LicenseUpgrade licenseUpgrade = licenseUpgradeRepository.findByBillingSetup_BillId(billingSetup.getBillId()).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));

                    if (Objects.nonNull(licenseUpgrade)) {
                        invalidateBill(licenseUpgrade.getInitialBillId());
                    }
                }

                //save to DB
                billingSetup = billingSetupRepository.save(billingSetup);

                //get package
                PackageDTO packages = packageService.getBillPackage(billingSetup.getBillId());

                //generate coupons
                //logBillCoupon(billingSetup , packages);

                //save to DB
                billingSetupRepository.save(billingSetup);

            } else {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.BILL_AMOUNT_IS_NOT_SAME_AS_PAID_AMOUNT);
            }
        }

    }

    public void invalidateBill(UUID billId) {

        BillingSetup billingSetup = billingSetupRepository.findByBillId(billId);

        billingSetup.setValidate(false);
        billingSetup.setUpdatedAt(LocalDateTime.now());

        billingSetupRepository.save(billingSetup);

    }

}
