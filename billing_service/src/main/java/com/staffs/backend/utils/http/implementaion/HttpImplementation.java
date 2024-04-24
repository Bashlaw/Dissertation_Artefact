package com.staffs.backend.utils.http.implementaion;

import com.staffs.backend.utils.http.HttpService;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class HttpImplementation implements HttpService {

    @Override
    public HttpResponse<JsonNode> post(Map<String, String> headerList, String jsonPayload, String url) {
        log.info("Making POST request with header {}, jsonPayload {} and url {}", headerList, jsonPayload, url);
        Unirest.config().verifySsl(false);
        return Unirest.post(url)
                .headers(headerList)
                .body(jsonPayload)
                .asJson();
    }

    @Override
    public HttpResponse<JsonNode> get(Map<String, String> headerList, Map<String, Object> params, String url) {
        log.info("Making GET request with header {}, params {} and url {}", headerList, params, url);
        GetRequest getRequest = Unirest.get(url).headers(headerList);
        if (Objects.isNull(params)) {
            return getRequest.asJson();
        } else {
            return getRequest.queryString(params).asJson();
        }
    }


}
