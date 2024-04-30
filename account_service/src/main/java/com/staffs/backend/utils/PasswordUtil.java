package com.staffs.backend.utils;

import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PasswordUtil {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL_CHARS = "@#$%^&+=";
    private static final String ALL_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL_CHARS;

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword(int length) {
        if (length < 8 || length > 20) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PASSWORD_LENGTH_SHOULD_BE_BETWEEN_8_AND_20);
        }

        StringBuilder password = new StringBuilder(length);
        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;

        do {
            password.setLength(0); // Clear the StringBuilder

            for (int i = 0; i < length; i++) {
                char randomChar = ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length()));
                password.append(randomChar);

                if (CHAR_LOWER.indexOf(randomChar) != -1) {
                    hasLower = true;
                } else if (CHAR_UPPER.indexOf(randomChar) != -1) {
                    hasUpper = true;
                } else if (NUMBER.indexOf(randomChar) != -1) {
                    hasDigit = true;
                } else if (SPECIAL_CHARS.indexOf(randomChar) != -1) {
                    hasSpecial = true;
                }
            }

            // Check if the password meets the criteria
        } while (!hasLower || !hasUpper || !hasDigit || !hasSpecial);

        return password.toString();
    }

    /**
     * ^ represents the starting character of the string.
     * (?=.*[0-9]) represents a digit must occur at least once.
     * (?=.*[a-z]) represents a lower case alphabet must occur at least once.
     * (?=.*[A-Z]) represents an upper case alphabet that must occur at least once.
     * (?=.*[@#$%^&-+=()] represents a special character that must occur at least once.
     * (?=\\S+$) white spaces don’t allow in the entire string.
     * .{8, 20} represents at least 8 characters and at most 20 characters.
     * $ represents the end of the string.
     */
    public static boolean isValidPassword(String password) {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#*$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the password is empty,
        // return false
        if (password == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(password);

        // Return if the password
        // matched the ReGex
        return m.matches();
    }

    public static void main(String[] args) {
        int length = 8; // Change the length as needed
        String password = generatePassword(length);
        log.info("Generated Password: {}" , password);
        log.info("Valid Password: {}" , isValidPassword(password));
    }

}
