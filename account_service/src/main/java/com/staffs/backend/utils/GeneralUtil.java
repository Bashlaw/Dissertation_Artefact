package com.staffs.backend.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GeneralUtil {


    public static boolean stringIsNullOrEmpty(String value) {
        return Objects.isNull(value) || value.isEmpty();
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

    public static String generateRandomChar(int charLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit , rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(charLength)
                .collect(StringBuilder::new , StringBuilder::appendCodePoint , StringBuilder::append)
                .toString();
    }

    public static String getCurrency(String code) {

        return switch (code) {
            case "Nigeria" -> "NGN";
            case "Kenya" -> "KES";
            case "United Arab Emirates" -> "AED";
            case "Angola" -> "AOA";
            case "South Africa" -> "ZAR";
            case "Zambia" -> "ZMW";
            case "Tanzania" -> "TZS";
            case "Zimbabwe" -> "ZWL";
            default -> "USD";
        };
    }

}
