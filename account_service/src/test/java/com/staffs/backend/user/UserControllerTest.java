package com.staffs.backend.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.staffs.backend.configuration.AccountSQLDatasourceConfig;
import com.staffs.backend.security.dto.UserLoginRequest;
import com.staffs.backend.user.dto.CreateUpdateUserDTO;
import com.staffs.backend.utils.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(AccountSQLDatasourceConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private String savedUserToken = null;

    private static final String CREATE_ADMIN_URL = "http://localhost:8050/api/v1/user/admin";
    private static final String AUTHENTICATION_URL = "http://localhost:8050/api/v1/authenticate";
    private static final String SIGNUP_CUSTOMER_URL = "http://localhost:8050/api/v1/user/customer";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void create_Customer_With_Normal_Value() throws JsonProcessingException, JSONException {

        //create a test double user
        CreateUpdateUserDTO dto = new CreateUpdateUserDTO();
        dto.setFirstName("Test");
        dto.setLastName("Tester");
        dto.setEmail("test" + GeneralUtil.generateRandomWord(5) + "@user.com");
        dto.setGender("MALE");
        dto.setDob("01/12/1994");
        dto.setPhoneNumber(GeneralUtil.generateCode(11));
        dto.setPassword("P@a55w0rd");

        HttpHeaders headers = getHeaders();

        //convert an object to json string
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(dto);

        HttpEntity<String> jwtEntity = new HttpEntity<>(json , headers);

        ResponseEntity<String> response = testRestTemplate.exchange(SIGNUP_CUSTOMER_URL , HttpMethod.POST , jwtEntity , String.class);
        log.info(response.getBody());
        JSONObject jsonObject = new JSONObject(response.getBody());
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON , response.getHeaders().getContentType());
        assertEquals(jsonObject.getString("responseMessage") , "OK");
        assertEquals(jsonObject.getInt("responseCode") , 200);

    }

    @Test
    public void create_Customer_WithInvalidValue_NoFirstname() throws JsonProcessingException, JSONException {

        //create a test double user
        CreateUpdateUserDTO dto = new CreateUpdateUserDTO();
        dto.setFirstName("");
        dto.setLastName("Tester");
        dto.setEmail("test" + GeneralUtil.generateRandomWord(5) + "@user.com");
        dto.setGender("FEMALE");
        dto.setDob("01/12/1994");
        dto.setPhoneNumber(GeneralUtil.generateCode(11));
        dto.setPassword("P@a55w0rd");

        HttpHeaders headers = getHeaders();

        //convert the object to json string
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(dto);

        HttpEntity<String> jwtEntity = new HttpEntity<>(json , headers);

        ResponseEntity<String> response = testRestTemplate.exchange(SIGNUP_CUSTOMER_URL , HttpMethod.POST , jwtEntity , String.class);
        log.info(response.getBody());
        JSONObject jsonObject = new JSONObject(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST , response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON , response.getHeaders().getContentType());
        assertEquals(jsonObject.getInt("responseCode") , 400);
        assertEquals(jsonObject.getString("responseMessage") , "first name cannot be empty");

    }


    @Test
    public void create_Customer_WithInvalidValue_AgeLessThan18() throws JsonProcessingException, JSONException {

        //create a test double user
        CreateUpdateUserDTO dto = new CreateUpdateUserDTO();
        dto.setFirstName("Test");
        dto.setLastName("Tester");
        dto.setEmail("test" + GeneralUtil.generateRandomWord(5) + "@user.com");
        dto.setGender("MALE");
        dto.setDob("17/01/2007");
        dto.setPhoneNumber(GeneralUtil.generateCode(11));
        dto.setPassword("P@a55w0rd");

        HttpHeaders headers = getHeaders();

        //convert the object to json string
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(dto);

        HttpEntity<String> jwtEntity = new HttpEntity<>(json , headers);

        ResponseEntity<String> response = testRestTemplate.exchange(SIGNUP_CUSTOMER_URL , HttpMethod.POST , jwtEntity , String.class);
        log.info(response.getBody());
        JSONObject jsonObject = new JSONObject(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST , response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON , response.getHeaders().getContentType());
        assertEquals(jsonObject.getInt("responseCode") , 400);
        assertEquals(jsonObject.getString("responseMessage") , "User must be older than 18 years");

    }

    @Test
    public void create_Admin_With_Normal_Value() throws JsonProcessingException, JSONException {

        //create a test double user
        CreateUpdateUserDTO dto = new CreateUpdateUserDTO();
        dto.setFirstName("TestAdmin");
        dto.setLastName("TesterAdmin");
        dto.setEmail("test" + GeneralUtil.generateRandomWord(6) + "@adminUser.com");
        dto.setPassword("P@a55w0rd");
        dto.setRoleId(1L);

        HttpHeaders headers = getHeaders();
        headers.set("Authorization" , getTokenAdmin());

        //convert the object to json string
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(dto);

        HttpEntity<String> jwtEntity = new HttpEntity<>(json , headers);

        ResponseEntity<String> response = testRestTemplate.exchange(CREATE_ADMIN_URL , HttpMethod.POST , jwtEntity , String.class);
        log.info(response.getBody());
        JSONObject jsonObject = new JSONObject(response.getBody());
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON , response.getHeaders().getContentType());
        assertEquals(jsonObject.getInt("responseCode") , 200);
        assertEquals(jsonObject.getString("responseMessage") , "OK");

    }

    @Test
    public void create_Admin_With_Invalid_Role() throws JsonProcessingException, JSONException {

        //create a test double user
        CreateUpdateUserDTO dto = new CreateUpdateUserDTO();
        dto.setFirstName("TestAdmin");
        dto.setLastName("TesterAdmin");
        dto.setEmail("test" + GeneralUtil.generateRandomWord(3) + "@adminUser.com");
        dto.setPassword("P@a55w0rd");
        dto.setRoleId(0L);

        HttpHeaders headers = getHeaders();
        headers.set("Authorization" , getTokenAdmin());

        //convert the object to json string
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(dto);

        HttpEntity<String> jwtEntity = new HttpEntity<>(json , headers);

        ResponseEntity<String> response = testRestTemplate.exchange(CREATE_ADMIN_URL , HttpMethod.POST , jwtEntity , String.class);
        log.info(response.getBody());
        JSONObject jsonObject = new JSONObject(response.getBody());
        log.info(jsonObject.getString("status"));
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON , response.getHeaders().getContentType());
        assertEquals(jsonObject.getString("status") , "FAILED");
        assertEquals(jsonObject.getString("responseCode") , "99");
        assertEquals(jsonObject.getString("failureReason") , "Role id is required");

    }

    @Test
    public void create_Admin_With_WithWeakPassword() throws JsonProcessingException, JSONException {

        //create a test double user
        CreateUpdateUserDTO dto = new CreateUpdateUserDTO();
        dto.setFirstName("TestAdmin");
        dto.setLastName("TesterAdmin");
        dto.setEmail("test" + GeneralUtil.generateRandomWord(3) + "@adminUser.com");
        dto.setPassword("password");
        dto.setRoleId(1L);

        HttpHeaders headers = getHeaders();
        headers.set("Authorization" , getTokenAdmin());

        //convert the object to json string
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(dto);

        HttpEntity<String> jwtEntity = new HttpEntity<>(json , headers);

        ResponseEntity<String> response = testRestTemplate.exchange(CREATE_ADMIN_URL , HttpMethod.POST , jwtEntity , String.class);
        log.info(response.getBody());
        JSONObject jsonObject = new JSONObject(response.getBody());
        log.info(jsonObject.getString("status"));
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON , response.getHeaders().getContentType());
        assertEquals(jsonObject.getString("status") , "FAILED");
        assertEquals(jsonObject.getString("responseCode") , "99");
        assertEquals(jsonObject.getString("failureReason") , "Password is not strong enough");

    }

    private String getTokenAdmin() throws JsonProcessingException {

        if (savedUserToken != null) {
            return savedUserToken;
        }

        // create the test user authentication object
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
