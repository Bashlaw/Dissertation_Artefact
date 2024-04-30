package com.staffs.backend.log.service.implementation;

import com.staffs.backend.entity.log.AccessLog;
import com.staffs.backend.entity.user.Users;
import com.staffs.backend.log.service.AccessLogService;
import com.staffs.backend.repository.log.AccessLogRepository;
import com.staffs.backend.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
public class AccessLogServiceImpl implements AccessLogService {

    private final UserService userService;
    private final AccessLogRepository accessLogRepository;

    public AccessLogServiceImpl(UserService userService, AccessLogRepository accessLogRepository) {
        this.userService = userService;
        this.accessLogRepository = accessLogRepository;
    }

    @Override
    public void logUserAccess(Long userId, String deviceInfo, String ipAddress, Long accessedService) {
        log.info("logging user access");

        if (Objects.nonNull(userId)) {
            //instantiate DB entities
            AccessLog accessLog = new AccessLog();

            //get user info
            Users user = userService.getUserById(userId);

            //set values
            accessLog.setUser(user);
            accessLog.setDeviceInfo(deviceInfo);
            accessLog.setIpAddress(ipAddress);
            accessLog.setAccessedService(accessedService);
            accessLog.setCreatedAt(LocalDateTime.now());

            //save to DB
            accessLogRepository.save(accessLog);

        }
    }

}
