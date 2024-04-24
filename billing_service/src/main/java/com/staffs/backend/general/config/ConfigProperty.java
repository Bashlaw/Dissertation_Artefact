package com.staffs.backend.general.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ConfigProperty {

    @Value("${dpo.address.url}")
    private String dpoUrl;

    @Value("${dpo.auth.url}")
    private String dpoAuthUrl;

    @Value("${dpo.push.url}")
    private String dpoPushUrl;

    @Value("${dpo.confirm.url}")
    private String dpoConfirmUrl;

    @Value("${dpo.cancel.url}")
    private String dpoCancelUrl;

    @Value("${dpo.refund.url}")
    private String dpoRefundUrl;

    private String dpoPaymentToken;

    @Value("${dpo.auth.username}")
    private String dpoUsername;

    @Value("${dpo.auth.password}")
    private String dpoPassword;

    @Value("${dpo.serviceType}")
    private String dpoServiceType;

    @Value("${mpesa.address.url}")
    private String mpesaUrl;

    @Value("${mpesa.auth.url}")
    private String mpesaAuthUrl;

    @Value("${mpesa.push.url}")
    private String mpesaPushUrl;

    @Value("${mpesa.confirm.url}")
    private String mpesaConfirmUrl;

    @Value("${mpesa.cancel.url}")
    private String mpesaCancelUrl;

    @Value("${mpesa.refund.url}")
    private String mpesaRefundUrl;

    private String mpesaPaymentToken;

    @Value("${mpesa.auth.username}")
    private String mpesaUsername;

    @Value("${mpesa.auth.password}")
    private String mpesaPassword;

    @Value("${mpesa.bill.number}")
    private String mpesaBillNumber;

    @Value("${mpesa.reference.id}")
    private String mpesaReferenceID;

    @Value("${paystack.address.url}")
    private String paystackUrl;

    @Value("${paystack.auth.url}")
    private String paystackAuthUrl;

    @Value("${paystack.push.url}")
    private String paystackPushUrl;

    @Value("${paystack.confirm.url}")
    private String paystackConfirmUrl;

    @Value("${paystack.cancel.url}")
    private String paystackCancelUrl;

    @Value("${paystack.refund.url}")
    private String paystackRefundUrl;

    private String paystackPaymentToken;

    @Value("${paystack.auth.username}")
    private String paystackUsername;

    @Value("${paystack.auth.password}")
    private String paystackPassword;

}
