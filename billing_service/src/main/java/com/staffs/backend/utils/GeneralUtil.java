package com.staffs.backend.utils;

import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GeneralUtil {

    public static String removePrefixAndSuffix(String value) {
        //if value is not null or empty
        if (value != null && !value.isEmpty()) {
            return value.substring(1 , value.length() - 1);
        } else {
            return null;
        }
    }

    public static String generateTransactionRef(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSSSSSSSS");

        String ref = UUID.randomUUID().toString().replaceAll("[^0-9]" , "");
        ref = ref.substring(0 , 5);

        return dateFormat.format(date) + ref;
    }

    public static boolean stringIsNullOrEmpty(String value) {
        return Objects.isNull(value) || value.isEmpty();
    }


    public static String generateCode(int length) {

        String digits = "0123456789";

        Random random = new Random();
        StringBuilder otpBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otpBuilder.append(digits.charAt(random.nextInt(digits.length())));
        }
        return otpBuilder.toString();
    }

    public static boolean checkIdentifierIfPhoneNumber(String identifier) {
        return !identifier.contains("@") && !isInvalidPhoneNumber(identifier);
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(EMAIL_REGEX);

        if (email == null) {
            return false;
        }

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public static boolean isInvalidPhoneNumber(String phoneNumber) {

        // Regex to check valid phone number.
        String regex = "^?\\d{11}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the phone number is empty, return true
        // return false
        if (phoneNumber == null) {
            return true;
        }

        // Pattern class contains matcher() method
        // to find matching between given phoneNumber
        // and regular expression.
        Matcher m = p.matcher(phoneNumber);

        // Return if the phoneNumber
        // matched the ReGex
        return !m.matches();
    }

    public static String getDateOfBirth(int age) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Subtract the age from the current year
        int birthYear = currentDate.getYear() - age;

        // Assume birthdate as January 1st of the birth year
        LocalDate birthDate = LocalDate.of(birthYear , 1 , 1);

        // Format the date as dd/MM/YYYY
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return birthDate.format(formatter);

    }

    public static PageImpl<?> getPage(int page , int size , boolean isDownload , TypedQuery<?> query) {
        Pageable paged;
        int totalRows;

        if (page > 0) {
            page = page - 1;
        }

        if (isDownload) {
            paged = PageRequest.of(0 , 1000000);
            totalRows = query.getResultList().size();
            return new PageImpl<>(query.getResultList() , paged , totalRows);
        }

        totalRows = query.getResultList().size();
        paged = PageRequest.of(page , size);

        query.setFirstResult(paged.getPageNumber() * paged.getPageSize());
        query.setMaxResults(paged.getPageSize());

        return new PageImpl<>(query.getResultList() , paged , totalRows);
    }

    public static String toSentenceCase(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return unchanged if input is null or empty
        }

        // Convert the first character to uppercase and the rest to lowercase
        String firstChar = input.substring(0 , 1).toUpperCase();
        String restOfString = input.substring(1).toLowerCase();

        // Concatenate and return the modified string
        return firstChar + restOfString;
    }

    public static Page<?> convertListToPage(List<?> list , Pageable pageable) {

        //set page
        int page = pageable.getPageNumber();
        if (page > 0) {
            page = page - 1;
        }

        return new PageImpl<>(list.subList(page * pageable.getPageSize() , list.size()) , PageRequest.of(page , pageable.getPageSize()) , list.size());
    }

}
