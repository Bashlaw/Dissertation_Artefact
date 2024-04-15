package com.staffs.backend.repository.log;

import com.staffs.backend.entity.log.ChangeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, String> {

    Optional<ChangeLog> findByLogId(String logId);

    Page<ChangeLog> findByUserIdOrderByCreatedAtDesc(Long adminUserId, Pageable pageable);

    Page<ChangeLog> findByModuleOrderByCreatedAtDesc(String module, Pageable pageable);

    Page<ChangeLog> findByUserIdAndModuleOrderByCreatedAtDesc(Long adminUserId, String module, Pageable pageable);

}
