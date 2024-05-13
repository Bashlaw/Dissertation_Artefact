package com.staffs.backend.general.service.implementation;

import com.google.gson.Gson;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.general.service.GeneralService;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneralServiceImpl implements GeneralService {

    @Value("${max-pull-size:100}")
    private int maxPullSize;

    private final Gson gson;

    //Used to format an object into a string
    @Override
    public String getAsString(Object o) {
        return gson.toJson(o);
    }

    //used to format the return value of the http post or get request
    @Override
    public String getResponseAsString(HttpResponse<JsonNode> response) {
        log.info("getting JSON response as a string");

        if (Objects.nonNull(response) && (Objects.nonNull(response.getBody()))) {
            String body = response.getBody().toPrettyString();
            log.info(body);
            return body;
        }
        throw new GeneralException(ResponseCodeAndMessage.ERROR_PROCESSING.responseCode , "No Response from Host");
    }


    @Override
    public HashMap<Integer, String> getResponseAsString(HttpResponse<JsonNode> response , boolean getStatus) {
        log.info("getting JSON response as a Map of body and status");

        HashMap<Integer, String> codeToResponse = new HashMap<>();

        if (Objects.nonNull(response)) {
            String body = response.getBody().toPrettyString();
            log.info(body);
            codeToResponse.put(response.getStatus() , body);
            return codeToResponse;
        }
        throw new GeneralException(ResponseCodeAndMessage.ERROR_PROCESSING.responseCode , "No Response from Host");
    }

    @Override
    public boolean isStringInvalid(String string) {
        return Objects.isNull(string) || string.trim().isEmpty();
    }

    @Override
    public BigDecimal getAmountAsBigDecimal(String amountString , boolean isKobo) {
        log.info("getting amount as decimal");

        BigDecimal amount;

        if (Objects.isNull(amountString)) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , "Invalid Amount");
        }

        if (amountString.isEmpty() || amountString.equals("0")) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , "Invalid Amount");
        } else {
            amount = new BigDecimal(amountString);
        }

        if (isKobo) {
            return amount.divide(BigDecimal.valueOf(100) , 4 , RoundingMode.HALF_UP);
        } else {
            return amount;
        }
    }

    @Override
    public Pageable getPageableObject(int size , int page) {
        log.info("Getting pageable object, initial size => {} and page {}" , size , page);

        Pageable paged;

        page = page - 1;

        if (page < 0) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , "Page minimum is 1");
        }

        if (size <= 0) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , "Size minimum is 1");
        }

        if (size > maxPullSize) {
            log.info("{} greater than max size of {}, defaulting to max" , size , maxPullSize);

            size = maxPullSize;
        }

        Sort sort = Sort.by(Sort.Direction.DESC , "createdAt");

        paged = PageRequest.of(page , size , sort);

        return paged;
    }

    //used to format failed response body
    @Override
    public Response prepareFailedResponse(int code , String message) {
        Response response = new Response();
        response.setResponseCode(code);
        response.setResponseMessage(message);

        log.info("ResponseCode => {} and message => {}" , code , message);

        return response;
    }

    @Override
    public Response prepareSuccessResponse(Object data) {
        Response response = new Response();

        response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL.responseCode);
        response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL.responseMessage);
        response.setData(data);

        log.info("Successful ResponseCode => {}" , data);

        return response;
    }

}
