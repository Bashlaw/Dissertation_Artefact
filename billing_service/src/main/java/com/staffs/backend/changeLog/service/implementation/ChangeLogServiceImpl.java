package com.staffs.backend.changeLog.service.implementation;

import com.staffs.backend.changeLog.dto.ChangeLogDTO;
import com.staffs.backend.changeLog.dto.ChangeLogListDTO;
import com.staffs.backend.changeLog.service.ChangeLogService;
import com.staffs.backend.entity.log.ChangeLog;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.repository.log.ChangeLogRepository;
import com.staffs.backend.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

    private final GeneralService generalService;

    private final ChangeLogRepository changeLogRepository;

    @Override
    public void logOperatorAction(String module , Long operator , String action) {
        log.info("logging operator actions");

        if (Objects.nonNull(operator)) {
            //instantiate DB entities
            ChangeLog changeLog = new ChangeLog();

            //set values
            changeLog.setLogId(UUID.randomUUID().toString());
            changeLog.setModule(module);
            changeLog.setUserId(operator);
            changeLog.setAction(action);
            changeLog.setCreatedAt(LocalDateTime.now());

            //save to DB
            changeLogRepository.save(changeLog);

        }
    }

    @Override
    public ChangeLogListDTO getChangeLogByOperator(PageableRequestDTO requestDTO , Long operatorId) {
        Pageable pageable = generalService.getPageableObject(requestDTO.getSize() , requestDTO.getPage());
        Page<ChangeLog> changeLogs = changeLogRepository.findByUserIdOrderByCreatedAtDesc(operatorId , pageable);

        return getChangeLogListDTO(changeLogs);
    }

    @Override
    public ChangeLogListDTO getChangeLogByModule(PageableRequestDTO requestDTO , String module) {
        Pageable pageable = generalService.getPageableObject(requestDTO.getSize() , requestDTO.getPage());
        Page<ChangeLog> changeLogs = changeLogRepository.findByModuleOrderByCreatedAtDesc(module , pageable);

        return getChangeLogListDTO(changeLogs);
    }

    @Override
    public ChangeLogListDTO getAuditLogByOperatorAndModule(PageableRequestDTO requestDTO , Long operatorId , String module) {
        Pageable pageable = generalService.getPageableObject(requestDTO.getSize() , requestDTO.getPage());
        Page<ChangeLog> changeLogs = changeLogRepository.findByUserIdAndModuleOrderByCreatedAtDesc(operatorId , module , pageable);

        return getChangeLogListDTO(changeLogs);
    }

    @Override
    public ChangeLogDTO getSingleLog(String logId) {
        return getChangeLogDTO(getSingleAuditLog(logId));
    }

    private ChangeLog getSingleAuditLog(String logId) {
        return changeLogRepository.findByLogId(logId).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private ChangeLogDTO getChangeLogDTO(ChangeLog changeLog) {
        log.info("converting change log to change log dto");

        ChangeLogDTO changeLogDTO = new ChangeLogDTO();
        BeanUtils.copyProperties(changeLog , changeLogDTO);

        //set log date
        changeLogDTO.setLogDate(DateUtil.localDateTimeToString(changeLog.getCreatedAt()));

        //set operator ID
        changeLogDTO.setOperatorId(changeLog.getUserId());

        return changeLogDTO;

    }

    private ChangeLogListDTO getChangeLogListDTO(Page<ChangeLog> changeLogPage) {
        log.info("Converting change log page to change log list dto");

        ChangeLogListDTO changeLogListDTO = new ChangeLogListDTO();

        List<ChangeLog> changeLogs = changeLogPage.getContent();
        if (!changeLogs.isEmpty()) {
            changeLogListDTO.setHasNextRecord(changeLogPage.hasNext());
            changeLogListDTO.setTotalCount((int) changeLogPage.getTotalElements());
        }

        List<ChangeLogDTO> changeLogDTOs = changeLogs.stream().map(this::getChangeLogDTO).toList();
        changeLogListDTO.setChangeLogDTOS(changeLogDTOs);

        return changeLogListDTO;

    }

}
