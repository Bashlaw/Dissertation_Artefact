package com.staffs.backend.paymentIntegration.service.implementation;

import com.google.gson.Gson;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.config.ConfigProperty;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.paymentIntegration.dto.*;
import com.staffs.backend.paymentIntegration.service.PaymentIntegrationService;
import com.staffs.backend.utils.http.HttpService;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONValue;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentIntegrationServiceImpl implements PaymentIntegrationService {

    private final String DPO = "DPO";
    private final String AUTH_DPO = "AUTH_DPO";
    private final String PUSH_DPO = "PUSH_DPO";
    private final String CONFIRM_DPO = "CONFIRM_DPO";
    private final String CANCEL_DPO = "CANCEL_DPO";
    private final String REFUND_DPO = "REFUND_DPO";

    private final String MPESA = "MPESA";
    private final String AUTH_MPESA = "AUTH_MPESA";
    private final String PUSH_MPESA = "PUSH_MPESA";
    private final String CONFIRM_MPESA = "CONFIRM_MPESA";
    private final String CANCEL_MPESA = "CANCEL_MPESA";
    private final String REFUND_MPESA = "REFUND_MPESA";

    private final String PAYSTACK = "PAYSTACK";
    private final String AUTH_PAYSTACK = "AUTH_PAYSTACK";
    private final String PUSH_PAYSTACK = "PUSH_PAYSTACK";
    private final String CONFIRM_PAYSTACK = "CONFIRM_PAYSTACK";
    private final String CANCEL_PAYSTACK = "CANCEL_PAYSTACK";
    private final String REFUND_PAYSTACK = "REFUND_PAYSTACK";

    private final Gson gson;
    private final HttpService httpService;
    private final ConfigProperty configProperty;
    private final GeneralService generalService;

    @Override
    public void getPaymentTokenDPO() {
        log.info("Get DPO payment auth token");

        Map<String, String> header = new HashMap<>();
        header.put("Content-Type" , "application/json");

        AuthRequest auth = AuthRequest.builder()
                .email(configProperty.getDpoUsername())
                .password(configProperty.getDpoPassword())
                .build();

        String requestBody = generalService.getAsString(auth);
        String url = getUrl(AUTH_DPO);

        //check host
        if (checkHost(url)) {
            log.error("Invalid Action!");
        } else {
            HttpResponse<JsonNode> responseObject = httpService.post(header , requestBody , url);

            if (Objects.equals(responseObject.getBody().getObject().get("message").toString() , "Login Successful!")) {
                String data = responseObject.getBody().getObject().get("data").toString();
                Object file = JSONValue.parse(data);
                JSONObject jsonObject = (JSONObject) file;
                String token = jsonObject.get("token").toString();
                //set to config
                configProperty.setDpoPaymentToken(token);
                log.error("Valid Action");
            } else {
                log.error("Invalid Actions");
            }
        }

    }

    @Override
    public PaymentRequestDTO generatePaymentRequestDPO(PaymentRequestDTO requestDTO) {
        return PaymentRequestDTO.builder()
                .accountId(requestDTO.getAccountId())
                .amount(requestDTO.getAmount())
                .backUrl(requestDTO.getBackUrl())
                .country(requestDTO.getCountry())
                .paymentType(requestDTO.getPaymentType())
                .currency(requestDTO.getCurrency())
                .description(requestDTO.getDescription())
                .serviceDate(requestDTO.getServiceDate())
                .serviceType(requestDTO.getServiceType())
                .client(requestDTO.getClient())
                .redirectUrl(requestDTO.getRedirectUrl())
                .transRef(requestDTO.getTransRef())
                .paymentSource(requestDTO.getPaymentSource()).build();
    }

    @Override
    public PaymentResponseDTO getPaymentResponse(PaymentRequestDTO requestDTO) {
        log.info("push request");

        Map<String, String> headers;
        String url;
        switch (requestDTO.getPaymentSource()) {
            case DPO:
                headers = getHeaderList(DPO);
                url = getUrl(PUSH_DPO);

                //set service type
                requestDTO.setServiceType(configProperty.getDpoServiceType());
                break;
            case MPESA:
                headers = getHeaderList(MPESA);
                url = getUrl(PUSH_MPESA);

                //set billNumber and referenceId
                requestDTO.setBillNumber(configProperty.getMpesaBillNumber());
                requestDTO.setReferenceId(configProperty.getMpesaReferenceID());
                break;
            case PAYSTACK:
                headers = getHeaderList(PAYSTACK);
                url = getUrl(PUSH_PAYSTACK);

                break;
            default:
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PAYMENT_SOURCE);
        }

        String requestBody = generalService.getAsString(requestDTO);

        HttpResponse<JsonNode> responseObject = httpService.post(headers , requestBody , url);
        String response = generalService.getResponseAsString(responseObject);
        return gson.fromJson(response , PaymentResponseDTO.class);

    }

    @Override
    public ConfirmResponseDTO getConfirmPaymentResponse(ConfirmRequestDTO requestDTO) {
        log.info("confirm payment");

        Map<String, String> headers;
        String url = switch (requestDTO.getPaymentSource()) {
            case DPO -> {
                headers = getHeaderList(DPO);
                yield getUrl(CONFIRM_DPO);
            }
            case MPESA -> {
                headers = getHeaderList(MPESA);
                yield getUrl(CONFIRM_MPESA);
            }
            case PAYSTACK -> {
                headers = getHeaderList(PAYSTACK);
                yield getUrl(CONFIRM_PAYSTACK);
            }
            default ->
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PAYMENT_SOURCE);
        };

        Map<String, Object> params = new HashMap<>();
        params.put("platform" , requestDTO.getPlatform());
        params.put("customerId" , requestDTO.getAccountId());
        params.put("companyRef" , requestDTO.getTransRef());

        HttpResponse<JsonNode> responseObject = httpService.get(headers , params , url);

        String response = generalService.getResponseAsString(responseObject);

        return gson.fromJson(response , ConfirmResponseDTO.class);

    }

    @Override
    public TransactionResponseDTO getCancelTransactionResponse(CancelRequestDTO requestDTO) {
        log.info("cancel payment transaction");

        Map<String, String> headers;
        String url = switch (requestDTO.getPaymentSource()) {
            case DPO -> {
                headers = getHeaderList(DPO);
                yield getUrl(CANCEL_DPO);
            }
            case MPESA -> {
                headers = getHeaderList(MPESA);
                yield getUrl(CANCEL_MPESA);
            }
            case PAYSTACK -> {
                headers = getHeaderList(PAYSTACK);
                yield getUrl(CANCEL_PAYSTACK);
            }
            default ->
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PAYMENT_SOURCE);
        };

        Map<String, Object> params = new HashMap<>();
        params.put("transaction_reference" , requestDTO.getTransactionReference());

        HttpResponse<JsonNode> responseObject = httpService.get(headers , params , url);

        String response = generalService.getResponseAsString(responseObject);

        return gson.fromJson(response , TransactionResponseDTO.class);
    }

    @Override
    public TransactionResponseDTO getRefundTransactionResponse(RefundRequestDTO requestDTO) {
        log.info("refund payment transaction");

        Map<String, String> headers;
        String url = switch (requestDTO.getPaymentSource()) {
            case DPO -> {
                headers = getHeaderList(DPO);
                yield getUrl(REFUND_DPO);
            }
            case MPESA -> {
                headers = getHeaderList(MPESA);
                yield getUrl(REFUND_MPESA);
            }
            case PAYSTACK -> {
                headers = getHeaderList(PAYSTACK);
                yield getUrl(REFUND_PAYSTACK);
            }
            default ->
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PAYMENT_SOURCE);
        };

        Map<String, Object> params = new HashMap<>();
        params.put("transaction_reference" , requestDTO.getTransactionReference());
        params.put("amount" , requestDTO.getAmount());

        HttpResponse<JsonNode> responseObject = httpService.get(headers , params , url);

        String response = generalService.getResponseAsString(responseObject);

        return gson.fromJson(response , TransactionResponseDTO.class);
    }

    @Override
    public void getPaymentTokenMPESA() {
        log.info("Get MPESA payment auth token");

        Map<String, String> header = new HashMap<>();
        header.put("Content-Type" , "application/json");

        AuthRequest auth = AuthRequest.builder()
                .email(configProperty.getMpesaUsername())
                .password(configProperty.getMpesaPassword())
                .build();

        String requestBody = generalService.getAsString(auth);
        String url = getUrl(AUTH_MPESA);

        //check host
        if (checkHost(url)) {
            log.error("Invalid action!");
        } else {
            HttpResponse<JsonNode> responseObject = httpService.post(header , requestBody , url);

            if (Objects.nonNull(responseObject) && responseObject.getCookies().contains("Original body:")) {
                if (Objects.equals(responseObject.getBody().getObject().get("message").toString() , "Login Successful")) {
                    String data = responseObject.getBody().getObject().get("data").toString();
                    Object file = JSONValue.parse(data);
                    JSONObject jsonObject = (JSONObject) file;
                    String token = jsonObject.get("token").toString();
                    //set to config
                    configProperty.setMpesaPaymentToken(token);

                } else {
                    log.error("Invalid action");
                }
            }
        }

    }

    @Override
    public PaymentRequestDTO generatePaymentRequestMPESA(PaymentRequestDTO requestDTO) {
        return PaymentRequestDTO.builder()
                .accountId(requestDTO.getAccountId())
                .amount(requestDTO.getAmount())
                .referenceId(requestDTO.getReferenceId())
                .billNumber(requestDTO.getBillNumber())
                .phoneNumber(requestDTO.getPhoneNumber())
                .transactionRef(requestDTO.getTransRef())
                .transactionDescription(requestDTO.getDescription())
                .client("PMS")
                .paymentSource(requestDTO.getPaymentSource()).build();
    }

    @Override
    public ConfirmRequestDTO generateConfirmRequestDPO(ConfirmRequestDTO dto) {
        return ConfirmRequestDTO.builder()
                .platform(dto.getPlatform())
                .accountId(dto.getAccountId())
                .transRef(dto.getTransRef())
                .paymentSource(dto.getPaymentSource()).build();
    }

    @Override
    public ConfirmRequestDTO generateConfirmRequestMPESA(ConfirmRequestDTO dto) {
        return ConfirmRequestDTO.builder()
                .platform(dto.getPlatform())
                .accountId(dto.getAccountId())
                .transactionRef(dto.getTransactionRef())
                .paymentSource(dto.getPaymentSource()).build();
    }

    @Override
    public ConfirmRequestDTO generateConfirmRequestGeneric(ConfirmRequestDTO dto) {
        return ConfirmRequestDTO.builder()
                .platform(dto.getPlatform())
                .transRef(dto.getTransRef())
                .paymentSource(dto.getPaymentSource()).build();
    }

    @Override
    public void getPaymentTokenPAYSTACK() {
        log.info("Get PAYSTACK payment auth token");

        Map<String, String> header = new HashMap<>();
        header.put("Content-Type" , "application/json");

        AuthRequest auth = AuthRequest.builder()
                .email(configProperty.getPaystackUsername())
                .password(configProperty.getPaystackPassword())
                .build();

        String requestBody = generalService.getAsString(auth);
        String url = getUrl(AUTH_PAYSTACK);

        //check host
        if (checkHost(url)) {
            log.error("invalid action");
        } else {
            HttpResponse<JsonNode> responseObject = httpService.post(header , requestBody , url);

            if (Objects.equals(responseObject.getBody().getObject().get("message").toString() , "Login Successful!")) {
                String data = responseObject.getBody().getObject().get("data").toString();
                Object file = JSONValue.parse(data);
                JSONObject jsonObject = (JSONObject) file;
                String token = jsonObject.get("token").toString();
                //set to config
                configProperty.setPaystackPaymentToken(token);

            } else {
                log.error("Invalid Actions!");
            }
        }

    }

    @Override
    public PaymentRequestDTO generatePaymentRequestPAYSTACK(PaymentRequestDTO requestDTO) {
        return PaymentRequestDTO.builder()
                .accountId(requestDTO.getAccountId())
                .amount(requestDTO.getAmount())
                .paymentType(requestDTO.getPaymentType())
                .currency(requestDTO.getCurrency())
                .redirectUrl(requestDTO.getRedirectUrl())
                .transRef(requestDTO.getTransRef())
                .client(requestDTO.getClient())
                .method("")
                .email(requestDTO.getEmail())
                .paymentSource(requestDTO.getPaymentSource()).build();
    }

    @Override
    public ConfirmRequestDTO generateConfirmRequestPAYSTACK(ConfirmRequestDTO dto) {
        return ConfirmRequestDTO.builder()
                .platform(dto.getPlatform())
                .accountId(dto.getAccountId())
                .transRef(dto.getTransRef())
                .paymentSource(dto.getPaymentSource()).build();
    }

    @Override
    public PaymentRequestDTO generatePaymentRequestGeneric(PaymentRequestDTO requestDTO) {
        return PaymentRequestDTO.builder()
                .accountId(requestDTO.getAccountId())
                .amount(requestDTO.getAmount())
                .country(requestDTO.getCountry())
                .paymentType(requestDTO.getPaymentType())
                .currency(requestDTO.getCurrency())
                .description(requestDTO.getDescription())
                .client(requestDTO.getClient())
                .transRef(requestDTO.getTransRef())
                .backUrl(requestDTO.getBackUrl())
                .redirectUrl(requestDTO.getRedirectUrl())
                .billReference(requestDTO.getBillReference())
                .paymentSource(requestDTO.getPaymentSource())
                .email(requestDTO.getEmail()).build();
    }

    private String getUrl(String type) {
        String dpoBaseUrl = configProperty.getDpoUrl();
        String mpesaBaseUrl = configProperty.getMpesaUrl();
        String paystackBaseUrl = configProperty.getPaystackUrl();

        return switch (type) {
            case AUTH_DPO -> dpoBaseUrl + configProperty.getDpoAuthUrl();
            case PUSH_DPO -> dpoBaseUrl + configProperty.getDpoPushUrl();
            case CONFIRM_DPO -> dpoBaseUrl + configProperty.getDpoConfirmUrl();
            case CANCEL_DPO -> dpoBaseUrl + configProperty.getDpoCancelUrl();
            case REFUND_DPO -> dpoBaseUrl + configProperty.getDpoRefundUrl();
            case AUTH_MPESA -> mpesaBaseUrl + configProperty.getMpesaAuthUrl();
            case PUSH_MPESA -> mpesaBaseUrl + configProperty.getMpesaPushUrl();
            case CONFIRM_MPESA -> mpesaBaseUrl + configProperty.getMpesaConfirmUrl();
            case CANCEL_MPESA -> mpesaBaseUrl + configProperty.getMpesaCancelUrl();
            case REFUND_MPESA -> mpesaBaseUrl + configProperty.getMpesaRefundUrl();
            case AUTH_PAYSTACK -> paystackBaseUrl + configProperty.getPaystackAuthUrl();
            case PUSH_PAYSTACK -> paystackBaseUrl + configProperty.getPaystackPushUrl();
            case CONFIRM_PAYSTACK -> paystackBaseUrl + configProperty.getPaystackConfirmUrl();
            case CANCEL_PAYSTACK -> paystackBaseUrl + configProperty.getPaystackCancelUrl();
            case REFUND_PAYSTACK -> paystackBaseUrl + configProperty.getPaystackRefundUrl();
            default ->
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_ACTION);
        };
    }

    private Map<String, String> getHeaderList(String source) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type" , "application/json");
        switch (source) {
            case DPO:
                header.put("Authorization" , "Bearer " + getToken(DPO));
                break;
            case MPESA:
                header.put("Authorization" , "Bearer " + getToken(MPESA));
                break;
            case PAYSTACK:
                header.put("Authorization" , "Bearer " + getToken(PAYSTACK));
                break;
        }
        return header;
    }

    private String getToken(String source) {
        return switch (source) {
            case DPO -> configProperty.getDpoPaymentToken();
            case MPESA -> configProperty.getMpesaPaymentToken();
            case PAYSTACK -> configProperty.getPaystackPaymentToken();
            default ->
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_ACTION);
        };
    }

    private boolean checkHost(String host) {
        URL url;
        HttpURLConnection con = null;

        try {
            url = new URL(host);
            con = (HttpURLConnection) url.openConnection();
            System.out.println(con.getResponseCode());
        } catch (IOException mue) {
            log.error("Http Exception! {}" , mue.getMessage());
            return true;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return false;
    }

}
