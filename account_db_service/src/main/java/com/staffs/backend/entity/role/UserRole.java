package com.staffs.backend.entity.role;

import com.staffs.backend.entity.permission.UserPermission;
import com.staffs.backend.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "admin_user_roles")
@Table(indexes = {
        @Index(name = "idx_adminuserrole_name", columnList = "name") ,
        @Index(name = "idx_adminuserrole_alias", columnList = "alias")
})
public class UserRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String alias;

    private String description;

    private boolean disabled = false;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    private List<UserPermission> permissionList;

}
