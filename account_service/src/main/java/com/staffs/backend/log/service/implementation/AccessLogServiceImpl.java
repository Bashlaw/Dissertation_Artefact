package com.staffs.backend.log.service.implementation;

import com.staffs.backend.entity.log.AccessLog;
import com.staffs.backend.log.service.AccessLogService;
import com.staffs.backend.repository.log.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessLogServiceImpl implements AccessLogService {

    private final AccessLogRepository accessLogRepository;

    @Override
    public void logUserAccess(Long userId, String deviceInfo, String ipAddress, Long accessedService) {
        log.info("logging user access");

        if (Objects.nonNull(userId)) {
            //instantiate DB entities
            AccessLog accessLog = new AccessLog();

            //set values
            accessLog.setUserId(userId);
            accessLog.setDeviceInfo(deviceInfo);
            accessLog.setIpAddress(ipAddress);
            accessLog.setAccessedService(accessedService);
            accessLog.setCreatedAt(LocalDateTime.now());

            //save to DB
            accessLogRepository.save(accessLog);

        }
    }

}
