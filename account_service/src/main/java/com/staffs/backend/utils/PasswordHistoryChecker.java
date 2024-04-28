package com.staffs.backend.utils;

import com.staffs.backend.general.config.ConfigProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import redis.clients.jedis.Jedis;

@Slf4j
@RequiredArgsConstructor
public class PasswordHistoryChecker {

    private static final int MAX_HISTORY_LENGTH = 5; // Maximum number of passwords to keep in history per user
    private static final String PASSWORD_HISTORY_PREFIX = "password_history:";

    private final ConfigProperty configProperty;

    private Jedis jedis;
    private PasswordEncoder passwordEncoder;

    public boolean isPasswordInHistory(String username , String userType , String newPassword) {
        String key = getPasswordHistoryKey(username , userType);
        // Check if the new password is in the user's password history
        log.info("Connected to Redis server at {}:{}" , configProperty.getRedisHost() , configProperty.getRedisHost());
        jedis = new Jedis(configProperty.getRedisHost() , configProperty.getRedisPort());
        passwordEncoder = new BCryptPasswordEncoder();
        return jedis.lrange(key , 0 , -1).stream().anyMatch(password -> passwordEncoder.matches(newPassword , password));
    }

    public void addToPasswordHistory(String username , String userType , String newPassword) {
        String key = getPasswordHistoryKey(username , userType);
        // Add the new password to the user's password history
        jedis = new Jedis(configProperty.getRedisHost() , configProperty.getRedisPort());
        jedis.lpush(key , passwordEncoder.encode(newPassword));
        // Trim the list to keep only the last MAX_HISTORY_LENGTH passwords
        jedis.ltrim(key , 0 , MAX_HISTORY_LENGTH - 1);
    }

    private String getPasswordHistoryKey(String username , String userType) {
        return PASSWORD_HISTORY_PREFIX + username + ":" + userType;
    }

    public static void main(String[] args) {
        String username = "exampleUser";
        String newPassword = "newPassword";
        String userType = "admin";

        PasswordHistoryChecker passwordHistoryChecker = new PasswordHistoryChecker(new ConfigProperty());

        if (passwordHistoryChecker.isPasswordInHistory(username , userType , newPassword)) {
            System.out.println("New password is in the history. Please choose a different password.");
        } else {
            passwordHistoryChecker.addToPasswordHistory(username , userType , newPassword);
            System.out.println("New password added to the history.");
        }
    }

}
