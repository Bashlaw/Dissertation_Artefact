package com.staffs.backend.security.service;

import com.staffs.backend.entity.user.Users;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.permission.dto.UserPermissionDTO;
import com.staffs.backend.repository.user.UsersRepository;
import com.staffs.backend.security.dto.BillingUser;
import com.staffs.backend.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public BillingUser loadUserByUsername(String username) throws UsernameNotFoundException {

        String email;
        String password;
        List<GrantedAuthority> permissions;

        Users user = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        //reset password before you can log in
        if (user.isResetPassword()) {
            throw new InternalAuthenticationServiceException(MessageConstant.USER_NEED_TO_CHANGE_PASSWORD_TO_THEIR_DESIRED_PASSWORD_AFTER_PASSWORD_RESET_TO_CONTINUE);
        }

        UserDTO userDTO = UserDTO.getUserDTO(user);
        email = userDTO.getEmail();
        password = user.getPassword();
        permissions = getGrantedAuthorities(userDTO.getRole().getUserPermissionList());

        return new BillingUser(email , password , permissions , user.getUserType().name());

    }

    private List<GrantedAuthority> getGrantedAuthorities(List<UserPermissionDTO> permissionDTOS) {
        return permissionDTOS.stream().map(permissionDTO -> new SimpleGrantedAuthority(permissionDTO.getName())).collect(Collectors.toList());
    }

    public void blacklistToken(String token) {
        String key = "token_" + token;
        redisTemplate.opsForSet().add(key , token);
    }

    public boolean isTokenBlacklisted(String token) {
        String key = "token_" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}