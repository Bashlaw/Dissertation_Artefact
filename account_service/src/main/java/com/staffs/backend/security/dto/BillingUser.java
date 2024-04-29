package com.staffs.backend.security.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;

@Getter
public class BillingUser extends User {

    private String userType;

    public BillingUser(String username , String password , Collection<? extends GrantedAuthority> authorities) {
        super(username , password , authorities);
    }

    public BillingUser(String username , String password , Collection<? extends GrantedAuthority> authorities , String userType) {
        super(username , password , authorities);
        this.userType = userType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BillingUser that = (BillingUser) o;
        return Objects.equals(userType , that.userType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode() , userType);
    }

}
