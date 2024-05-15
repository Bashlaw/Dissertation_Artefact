package com.staffs.backend.user.service.implementation;

import com.staffs.backend.entity.otp.OTP;
import com.staffs.backend.entity.role.UserRole;
import com.staffs.backend.entity.user.Users;
import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.config.ConfigProperty;
import com.staffs.backend.general.dto.ChangePasswordRequestDTO;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.otp.service.OTPService;
import com.staffs.backend.repository.user.UsersRepository;
import com.staffs.backend.role.dto.UserRoleDTO;
import com.staffs.backend.role.service.UserRoleService;
import com.staffs.backend.startup.StartupConstant;
import com.staffs.backend.user.dto.CreateUpdateUserDTO;
import com.staffs.backend.user.dto.UserDTO;
import com.staffs.backend.user.dto.UserListDTO;
import com.staffs.backend.user.service.UserService;
import com.staffs.backend.utils.DateUtil;
import com.staffs.backend.utils.GeneralUtil;
import com.staffs.backend.utils.PasswordHistoryChecker;
import com.staffs.backend.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final OTPService otpService;
    private final ConfigProperty configProperty;
    private final GeneralService generalService;
    private final UserRoleService userRoleService;
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void createUser(CreateUpdateUserDTO dto , String performedBy) {
        log.info("creating new user {}" , dto.getEmail());

        boolean alreadyExist = usersRepository.existsByEmailAndPhoneNumber(dto.getEmail() , dto.getPhoneNumber());
        if (alreadyExist) {
            throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , "User with " + dto.getEmail() + " already registered or phone number already registered");
        }

        //validate email
        if (!GeneralUtil.isValidEmail(dto.getEmail())) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_EMAIL);
        }

        //validate phone number
        if (GeneralUtil.isInvalidPhoneNumber(dto.getPhoneNumber())) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PHONE_NUMBER);
        }

        if (!PasswordUtil.isValidPassword(dto.getPassword())) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PASSWORD_IS_NOT_STRONG_ENOUGH);
        }

        if (dto.getDob().split("/").length != 3) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_DATE_OF_BIRTH_FORMAT);
        }

        if (DateUtil.lessThan18(dto.getDob())) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.USER_MUST_BE_OLDER_THAN_18_YEARS);
        }

        //check if gender is valid
        if (!dto.getGender().equalsIgnoreCase("MALE") && !dto.getGender().equalsIgnoreCase("FEMALE")) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_GENDER);
        }

        Users user = new Users();

        BeanUtils.copyProperties(dto , user);

        //set encrypted random password
        String defaultPassword = PasswordUtil.generatePassword(8);
        if (GeneralUtil.stringIsNullOrEmpty(dto.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(defaultPassword));
        } else {
            defaultPassword = dto.getPassword();
            user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        }

        if (performedBy != null) {
            user.setUserType(UserType.ADMIN);
            if (dto.getRoleId() == null || dto.getRoleId() <= 0) {
               throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.ROLE_ID_IS_REQUIRED);
            }

            //set user role
            UserRole userRole = userRoleService.getRoleById(dto.getRoleId());
            user.setUserRole(userRole);

        } else {
            user.setUserType(UserType.CUSTOMER);
        }

        //save to DB
        usersRepository.save(user);

        //log to password history
        PasswordHistoryChecker passwordHistoryChecker = new PasswordHistoryChecker(configProperty);
        passwordHistoryChecker.addToPasswordHistory(user.getEmail() , UserType.ADMIN.name() , defaultPassword);

    }

    @Override
    public UserDTO updateUser(CreateUpdateUserDTO dto , String performedBy) {
        log.info("updating user {}" , dto);

        Users user = usersRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.USER_NOT_FOUND));

        //check if phone number exists for a different user
        if (usersRepository.existsByPhoneNumberAndEmailNot(dto.getPhoneNumber() , dto.getEmail())) {
            throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , "Phone number already registered to another user");
        }

        //check deleted
        if (user.isDeleted()) {
            throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_ALREADY_DELETED);
        }

        //check disabled
        if (user.isDisabled()) {
            throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_IS_DISABLED);
        }

        //validate phone number
        if (GeneralUtil.isInvalidPhoneNumber(dto.getPhoneNumber())) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PHONE_NUMBER);
        }

        BeanUtils.copyProperties(dto , user);

        //set user role
        UserRoleDTO userRoleDTO = userRoleService.getRoleDTO(dto.getRoleId());
        UserRole userRole = userRoleService.getRoleById(userRoleDTO.getId());
        user.setUserRole(userRole);
        user.setDob(dto.getDob());
        user.setGender(dto.getGender());

        if (performedBy != null) {
            user.setUserType(UserType.ADMIN);
        } else {
            user.setUserType(UserType.CUSTOMER);
        }

        //save to DB
        user = usersRepository.save(user);

        return UserDTO.getUserDTO(user);

    }

    @Override
    public UserDTO getUserDTO(String email) {

        //get admin user info
        Users user = getUser(email);

        //check deleted
        if (user.isDeleted()) {
            throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_ALREADY_DELETED);
        }

        //check disabled
        if (user.isDisabled()) {
            throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_IS_DISABLED);
        }

        if (user.isResetPassword()) {
            throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_NEED_TO_CHANGE_PASSWORD_TO_THEIR_DESIRED_PASSWORD_AFTER_PASSWORD_RESET_TO_CONTINUE);
        }

        return UserDTO.getUserDTO(user);
    }

    @Override
    public UserListDTO getUserDTOs(PageableRequestDTO dto) {

        Pageable pageable = generalService.getPageableObject(dto.getSize() , dto.getPage());

        return getUserListDTO(usersRepository.findAll(pageable));
    }

    @Override
    public void requestResetPassword(String identifier) {
        log.info("forget password request for user {}" , identifier);

        //instantiate user
        Users users;

        //check if email or phone number
        if (GeneralUtil.checkIdentifierIfPhoneNumber(identifier)) {
            users = getAdminUserByPhoneNumber(identifier);
        } else {
            users = getUser(identifier);
        }

        if (Objects.nonNull(users)) {
            if (users.isDeleted()) {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_IS_DELETED);
            }

            if (users.isDisabled()) {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_IS_DISABLED);
            }

            //generate and send token
            otpService.generateOTP(identifier , UserType.ADMIN , 4);

            users.setResetPassword(true);
            users.setUpdatedAt(LocalDateTime.now());

            usersRepository.save(users);

        }

    }

    @Override
    public void changePassword(String email , ChangePasswordRequestDTO dto) {
        log.info("changing password for user {}" , email);

        Users users = getUser(email);

        //check old password
        BCryptPasswordEncoder b = new BCryptPasswordEncoder();
        if (!b.matches(dto.getOldPassword() , users.getPassword())) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.WRONG_OLD_PASSWORD_INSERTED);
        } else {
            if (users.isDeleted()) {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_IS_DELETED);
            }

            if (users.isDisabled()) {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_IS_DISABLED);
            }

            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PASSWORD_DO_NOT_MATCH);
            }

            if (!PasswordUtil.isValidPassword(dto.getPassword())) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PASSWORD_IS_NOT_STRONG_ENOUGH);
            }

            if (dto.getOldPassword().equals(dto.getPassword())) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.OLD_PASSWORD_IS_SAME_AS_NEW_PASSWORD);
            }

            PasswordHistoryChecker passwordHistoryChecker = new PasswordHistoryChecker(configProperty);

            if (passwordHistoryChecker.isPasswordInHistory(email , UserType.ADMIN.name() , dto.getPassword())) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PASSWORD_IS_IN_HISTORY);
            }

            if (users.isResetPassword()) {
                users.setResetPassword(false);
            }

            users.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
            users.setUpdatedAt(LocalDateTime.now());

            //save adminUser
            usersRepository.save(users);

            //log new password to password history
            passwordHistoryChecker.addToPasswordHistory(email , UserType.ADMIN.name() , dto.getPassword());

            //log old password to password history
            passwordHistoryChecker.addToPasswordHistory(email , UserType.ADMIN.name() , dto.getOldPassword());

        }

    }

    @Override
    public void disableUser(String email) {
        log.info("disabling system user {}" , email);

        Users user = getUser(email);

        if (!user.isDisabled()) {
            user.setDisabled(true);
            user.setUpdatedAt(LocalDateTime.now());
            usersRepository.save(user);
        } else {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_DISABLED.responseCode , MessageConstant.USER_ALREADY_DISABLED);
        }

    }

    @Override
    public void enableUser(String email) {
        log.info("enabling system user {}" , email);

        Users user = getUser(email);

        if (user.isDisabled()) {
            user.setDisabled(false);
            user.setUpdatedAt(LocalDateTime.now());
            usersRepository.save(user);
        } else {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.USER_IS_ENABLED);
        }

    }

    @Override
    public void createSuperSystemUser(UserRole userRole) {
        log.info("creating super system user...");

        if (!usersRepository.existsByEmail(StartupConstant.DEFAULT_SYSTEM_ADMIN_EMAIL)) {
            log.info("creating super system user if not exist");

            Users user = new Users();
            user.setEmail(StartupConstant.DEFAULT_SYSTEM_ADMIN_EMAIL);
            user.setFirstName(StartupConstant.DEFAULT_SYSTEM_ADMIN_FIRST_NAME);
            user.setLastName(StartupConstant.DEFAULT_SYSTEM_ADMIN_LAST_NAME);
            user.setResetPassword(false);
            user.setPassword(bCryptPasswordEncoder.encode(StartupConstant.DEFAULT_SYSTEM_ADMIN_PASSWORD));
            user.setUserRole(userRole);
            user.setUserType(UserType.ADMIN);

            //save to DB
            user = usersRepository.save(user);

            log.info("System creates super system user {}" , user);

        }

    }

    @Override
    public void validateForgetPasswordToken(String token , String identifier) {
        log.info("validating forget password token for {}" , identifier);

        //instantiate user
        Users user;

        //check if email or phone number
        if (GeneralUtil.checkIdentifierIfPhoneNumber(identifier)) {
            user = getAdminUserByPhoneNumber(identifier);
        } else {
            user = getUser(identifier);
        }

        if (Objects.nonNull(user)) {
            if (user.isDeleted()) {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_IS_DELETED);
            }

            if (user.isDisabled()) {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_IS_DISABLED);
            }

            if (otpService.verifyOTP(identifier , UserType.ADMIN , token)) {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.INVALID_OTP);
            }

            //update otp to use
            otpService.setUsed(otpService.getOtp(identifier , UserType.ADMIN));

        }

    }

    @Override
    public void resetPassword(String password , String confirmPassword , String identifier) {
        log.info("resetting password for user {}" , identifier);

        //instantiate user
        Users user;

        //check if email or phone number
        if (GeneralUtil.checkIdentifierIfPhoneNumber(identifier)) {
            user = getAdminUserByPhoneNumber(identifier);
        } else {
            user = getUser(identifier);
        }

        if (Objects.nonNull(user)) {

            if (user.isDeleted()) {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_IS_DELETED);
            }

            if (user.isDisabled()) {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_IS_DISABLED);
            }

            OTP otp = otpService.getOtp(identifier , UserType.ADMIN);

            if (Objects.isNull(otp)) {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.INVALID_OTP);
            }

            //check if otp is valid
            otpService.validate(identifier , UserType.ADMIN);

            if (!confirmPassword.equals(password)) {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.PASSWORD_DO_NOT_MATCH);
            }

            if (!PasswordUtil.isValidPassword(password)) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PASSWORD_IS_NOT_STRONG_ENOUGH);
            }

            PasswordHistoryChecker passwordHistoryChecker = new PasswordHistoryChecker(configProperty);

            if (passwordHistoryChecker.isPasswordInHistory(user.getEmail() , UserType.ADMIN.name() , password)) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PASSWORD_IS_IN_HISTORY);
            }

            if (user.isResetPassword()) {
                user.setResetPassword(false);
            } else {
                throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.INVALID_REQUEST);
            }

            user.setPassword(new BCryptPasswordEncoder().encode(password));
            user.setUpdatedAt(LocalDateTime.now());

            //save to DB
            usersRepository.save(user);

            //log to password history
            passwordHistoryChecker.addToPasswordHistory(user.getEmail() , UserType.ADMIN.name() , password);

        }

    }

    @Override
    public void deleteUser(String email) {
        log.info("deleting user...");

        //get user
        Users user = getUser(email);

        if (user.isDeleted()) {
            throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_ALREADY_DELETED);
        }

        user.setDeleted(true);
        user.setUpdatedAt(LocalDateTime.now());

        //save to DB
        usersRepository.save(user);
    }

    @Override
    public Users getUser(String email) {
        return usersRepository.findByEmail(email).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.USER_NOT_FOUND));
    }

    @Override
    public Users getUserById(Long id) {
        return usersRepository.findById(id).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.USER_NOT_FOUND));
    }

    @Override
    public UserListDTO getAllCustomer(PageableRequestDTO dto) {

        Page<Users> userPage = usersRepository.findAllByUserType(UserType.CUSTOMER , generalService.getPageableObject(dto.getSize() , dto.getPage()));

        return getUserListDTO(userPage);
    }

    @Override
    public UserListDTO getAllAdmin(PageableRequestDTO dto) {

        Page<Users> userPage = usersRepository.findAllByUserType(UserType.ADMIN , generalService.getPageableObject(dto.getSize() , dto.getPage()));

        return getUserListDTO(userPage);
    }

    private Users getAdminUserByPhoneNumber(String phoneNumber) {
        return usersRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.USER_NOT_FOUND));
    }

    private UserListDTO getUserListDTO(Page<Users> userPage) {
        log.info("Converting user page to user list dto");

        UserListDTO userListDTO = new UserListDTO();

        List<Users> userList = userPage.getContent();
        if (!userList.isEmpty()) {
            userListDTO.setHasNextRecord(userPage.hasNext());
            userListDTO.setTotalCount((int) userPage.getTotalElements());
        }

        List<UserDTO> adminAdminUserDTOS = userList.stream().map(UserDTO::getUserDTO).toList();
        userListDTO.setAdminUserList(adminAdminUserDTOS);

        return userListDTO;
    }

}
