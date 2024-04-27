package com.staffs.backend.exceptions;

import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.general.service.GeneralService;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    private final GeneralService generalService;

    public ExceptionController(GeneralService generalService) {
        this.generalService = generalService;
    }

    private static String getFirstErrorMessage(Map<String, String> errors) {
        String firstErrorMessage;
        if (errors.isEmpty()) {
            firstErrorMessage = null;
        } else {
            if (!errors.containsValue(errors.values().iterator().next())) firstErrorMessage = null;
            else firstErrorMessage = errors.values().iterator().next();
        }
        return firstErrorMessage;
    }

    @ExceptionHandler({GeneralException.class})
    public final ResponseEntity<Response> handleException(Exception ex) {
        log.info("Error occurred, error message is {}", ex.getMessage());

        if (ex instanceof GeneralException) {
            Response response = generalService.prepareFailedResponse(Integer.parseInt(ex.getMessage()), ex.getCause().getMessage());
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getResponseCode()));
        } else {
            Response response = generalService.prepareFailedResponse(ResponseCodeAndMessage.ERROR_PROCESSING.responseCode, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String firstErrorMessage = getFirstErrorMessage(errors);
        Response response = generalService.prepareFailedResponse(ResponseCodeAndMessage.BAD_REQUEST.responseCode, firstErrorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String errorMessage = "Invalid request body. Please check your request data." + ex.getMessage();
        Response response = generalService.prepareFailedResponse(ResponseCodeAndMessage.BAD_REQUEST.responseCode, errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PSQLException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Response> handlePSQLException(PSQLException ex) {
        String errorMessage = "Database error; " + ex.getMessage();
        Response response = generalService.prepareFailedResponse(ResponseCodeAndMessage.ALREADY_EXIST.responseCode, errorMessage);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleIllegalArgumentException(IllegalArgumentException ex) {
        String errorMessage = "Illegal argument; " + ex.getMessage();
        Response response = generalService.prepareFailedResponse(ResponseCodeAndMessage.BAD_REQUEST.responseCode, errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleParseException(ParseException ex) {
        String errorMessage = "Unparseable; " + ex.getMessage();
        Response response = generalService.prepareFailedResponse(ResponseCodeAndMessage.BAD_REQUEST.responseCode, errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleNullException(NullPointerException ex) {
        String errorMessage = "Null pointer exception; " + ex.getMessage();
        Response response = generalService.prepareFailedResponse(ResponseCodeAndMessage.BAD_REQUEST.responseCode, errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
