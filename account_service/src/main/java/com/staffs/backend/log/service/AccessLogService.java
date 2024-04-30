package com.staffs.backend.log.service;

public interface AccessLogService {

    void logUserAccess(Long userId, String deviceInfo, String ipAddress, Long accessedService);

}
