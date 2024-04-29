package com.staffs.backend.general.service;

import com.staffs.backend.general.dto.RequestExtraInfo;
import com.staffs.backend.general.dto.Response;
import jakarta.servlet.http.HttpServletRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.HashMap;

public interface GeneralService {

    String getAsString(Object o);

    //used to format the return value of the http post or get request
    String getResponseAsString(HttpResponse<JsonNode> response);

    HashMap<Integer, String> getResponseAsString(HttpResponse<JsonNode> response , boolean getStatus);

    boolean isStringInvalid(String string);

    BigDecimal getAmountAsBigDecimal(String amountString , boolean isKobo);

    Pageable getPageableObject(int size , int page);

    //used to format failure response body
    Response prepareFailedResponse(int code , String message);

    //used to format success response body
    Response prepareSuccessResponse(Object data);

    RequestExtraInfo getRequestExtraInfo(HttpServletRequest request);

}
