package com.staffs.backend.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
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

}
