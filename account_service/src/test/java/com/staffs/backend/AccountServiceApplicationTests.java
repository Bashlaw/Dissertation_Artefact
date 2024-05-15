package com.staffs.backend;

import com.staffs.backend.configuration.AccountSQLDatasourceConfig;
import com.staffs.backend.permission.service.UserPermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Import(AccountSQLDatasourceConfig.class)
class AccountServiceApplicationTests {

    @Autowired
    private UserPermissionService permissionController;

    @Test
    void contextLoads() {
        assertNotNull(permissionController);
    }

}
