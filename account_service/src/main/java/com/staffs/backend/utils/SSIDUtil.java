package com.staffs.backend.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class SSIDUtil {

    private static final Random random = new Random();

    // Generates a random 9-digit number
    private static String generateRandomNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10)); // Generates digits 0-9
        }
        return sb.toString();
    }

    // Calculates the ssid checksum digit for the given number
    private static int calculateChecksum(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = number.charAt(i) - '0';
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }

    // Generates a complete 10-digit number with checksum
    public static String generateSSID() {
        String partialNumber = generateRandomNumber();
        int checksum = calculateChecksum(partialNumber);
        return partialNumber + checksum;
    }

    // Validates a 10-digit number using the ssid algorithm
    public static boolean isValidNumber(String number) {
        int checksum = number.charAt(number.length() - 1) - '0';
        String partialNumber = number.substring(0, number.length() - 1);
        return calculateChecksum(partialNumber) == checksum;
    }

    public static void main(String[] args) {
        String number = generateSSID();
        log.info("Generated Number: {}", number);
        number = "KW" + number;
        log.info("Checksum: {}", number.substring(2));

        // Check if the generated number is valid
        if (isValidNumber(number)) {
            log.info("Valid Number");
        } else {
            log.info("Invalid Number");
        }
    }

}
