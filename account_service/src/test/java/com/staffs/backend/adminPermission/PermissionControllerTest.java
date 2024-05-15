package com.staffs.backend.adminPermission;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staffs.backend.configuration.AccountSQLDatasourceConfig;
import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.permission.controller.UserPermissionController;
import com.staffs.backend.permission.dto.UserPermissionDTO;
import com.staffs.backend.permission.service.UserPermissionService;
import com.staffs.backend.security.dto.UserLoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(AccountSQLDatasourceConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PermissionControllerTest {

    private String savedUserToken = null;

    private static final String AUTHENTICATION_URL = "http://localhost:8050/api/v1/authenticate";
    private static final String GET_ADMIN_PERMISSION_URL = "http://localhost:8050/api/v1/permissions/admin";
    private static final String GET_CUSTOMER_PERMISSION_URL = "http://localhost:8050/api/v1/permissions/customer";

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    public void when_noPermission_for_admin_expect_emptyListOfPermissionDTO() {

        UserPermissionService mockPermissionService = mock(UserPermissionService.class);
        when(mockPermissionService.getAllUserPermissions(UserType.ADMIN)).thenReturn(new ArrayList<>());

        List<UserPermissionDTO> permissionDTOList = mockPermissionService.getAllUserPermissions(UserType.ADMIN);

        assertEquals(0 , permissionDTOList.size());
    }

    @Test
    public void when_noPermission_for_customer_expect_emptyListOfPermissionDTO() {

        UserPermissionService mockPermissionService = mock(UserPermissionService.class);
        when(mockPermissionService.getAllUserPermissions(UserType.CUSTOMER)).thenReturn(new ArrayList<>());

        List<UserPermissionDTO> permissionDTOList = mockPermissionService.getAllUserPermissions(UserType.CUSTOMER);

        assertEquals(0 , permissionDTOList.size());
    }

    @Test
    public void when_GetAllAdminPermission_expect_OneCallToPermissionService_GetAllPermissionDTO() {

        GeneralService mockGeneralService = mock(GeneralService.class);
        UserPermissionService mockPermissionService = mock(UserPermissionService.class);

        UserPermissionController permissionController = new UserPermissionController(mockGeneralService , mockPermissionService);

        permissionController.getAllSystemAdminPermissions();

        verify(mockPermissionService , times(1)).getAllUserPermissions(UserType.ADMIN);
        verifyNoMoreInteractions(mockPermissionService);
    }

    @Test
    public void when_GetAllCustomerPermission_expect_OneCallToPermissionService_GetAllPermissionDTO() {

        GeneralService mockGeneralService = mock(GeneralService.class);
        UserPermissionService mockPermissionService = mock(UserPermissionService.class);

        UserPermissionController permissionController = new UserPermissionController(mockGeneralService , mockPermissionService);

        permissionController.getAllSystemAdminPermissions();

        verify(mockPermissionService , times(1)).getAllUserPermissions(UserType.CUSTOMER);
        verifyNoMoreInteractions(mockPermissionService);
    }

    @Test
    public void when_NoAdminAuth_ThrowUnauthorized_To_GetPermission() {

        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(GET_ADMIN_PERMISSION_URL , String.class);

        assertEquals(HttpStatus.UNAUTHORIZED , responseEntity.getStatusCode());

    }

    @Test
    public void when_NoCustomerAuth_ThrowUnauthorized_To_GetPermission() {

        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(GET_CUSTOMER_PERMISSION_URL , String.class);

        assertEquals(HttpStatus.UNAUTHORIZED , responseEntity.getStatusCode());

    }

    @Test
    public void when_Admin_Auth_GetPermission() throws JsonProcessingException {

        HttpHeaders headers = getHeaders();
        headers.set("Authorization" , getTokenAdmin());

        HttpEntity<String> jwtEntity = new HttpEntity<>(null , headers);

        ResponseEntity<String> response = testRestTemplate.exchange(GET_ADMIN_PERMISSION_URL , HttpMethod.GET , jwtEntity , String.class);

        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON , response.getHeaders().getContentType());

    }

    @Test
    public void when_Customer_Auth_GetPermission() throws JsonProcessingException {

        HttpHeaders headers = getHeaders();
        headers.set("Authorization" , getTokenAdmin());

        HttpEntity<String> jwtEntity = new HttpEntity<>(null , headers);

        ResponseEntity<String> response = testRestTemplate.exchange(GET_CUSTOMER_PERMISSION_URL , HttpMethod.GET , jwtEntity , String.class);

        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON , response.getHeaders().getContentType());

    }

    private String getTokenAdmin() throws JsonProcessingException {

        if (savedUserToken != null) {
            return savedUserToken;
        }

        // create a user authentication object
        UserLoginRequest authenticationUser = getAuthenticationUser();
        // convert the user authentication object to JSON
        String authenticationBody = getBody(authenticationUser);

        // create headers specifying that it is JSON request
        HttpHeaders authenticationHeaders = getHeaders();
        HttpEntity<String> authenticationEntity = new HttpEntity<>(authenticationBody , authenticationHeaders);

        // Authenticate User and get JWT
        ResponseEntity<String> authenticationResponse = testRestTemplate.exchange(AUTHENTICATION_URL ,
                HttpMethod.POST , authenticationEntity , String.class);

        // if the authentication is successful
        if (authenticationResponse.getStatusCode().equals(HttpStatus.OK)) {
            String[] res = new String[]{(Arrays.toString(Objects.requireNonNull(authenticationResponse.getBody()).split(":")))};
            String[] res1 = Arrays.toString(res).split(",");
            String token = "Bearer " + res1[1];
            token = token.replaceAll("[}\\[\\]\"]" , "");

            savedUserToken = token;
            return token;
        }

        return null;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type" , MediaType.APPLICATION_JSON_VALUE);
        headers.set("Accept" , MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    private UserLoginRequest getAuthenticationUser() {
        UserLoginRequest user = new UserLoginRequest();
        user.setEmail("supersystemuser@optima.com");
        user.setPassword("P@ssw0rd");
        return user;
    }

    private String getBody(final UserLoginRequest user) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(user);
    }

}
