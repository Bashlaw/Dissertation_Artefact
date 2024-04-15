package com.staffs.backend.repository.log;

import com.staffs.backend.entity.log.AccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    Page<AccessLog> findByUser_EmailOrderByCreatedAtDesc(String email, Pageable pageable);

}
