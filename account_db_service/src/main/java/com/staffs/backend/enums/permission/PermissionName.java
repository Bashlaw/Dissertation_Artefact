package com.staffs.backend.enums.permission;

import com.staffs.backend.enums.user.UserType;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public enum PermissionName {

    //FUNCTIONAL PERMISSIONS
    //ADMIN USER
    CREATE_SYSTEM_ADMIN("Create system admin" , UserType.ADMIN),
    VIEW_SYSTEM_ADMIN("View system admin" , UserType.ADMIN),
    UPDATE_SYSTEM_ADMIN("Update system admin" , UserType.ADMIN),
    DELETE_SYSTEM_ADMIN("Delete system admin" , UserType.ADMIN),
    DEACTIVATE_ACTIVATE_SYSTEM_ADMIN("Deactivate/Activate system admin" , UserType.ADMIN),

    //DISABLED PERSON
    APPROVE_DISABLED_PERSON("Approve disabled person" , UserType.ADMIN),

    //CUSTOMER USER
    CREATE_CUSTOMER("Create customer" , UserType.CUSTOMER),
    VIEW_CUSTOMER("View customer" , List.of(UserType.CUSTOMER , UserType.ADMIN)),
    VIEW_CUSTOMERS("View customer" , UserType.ADMIN),
    UPDATE_CUSTOMER("Update customer" , UserType.CUSTOMER),
    DELETE_CUSTOMER("Delete customer" , UserType.CUSTOMER),
    DEACTIVATE_ACTIVATE_CUSTOMER("Deactivate/Activate customer" , UserType.ADMIN),

    //CLIENT
    CREATE_CLIENT("Create client" , UserType.ADMIN),
    VIEW_CLIENT("View client" , UserType.ADMIN),
    UPDATE_CLIENT("Update client" , UserType.ADMIN),
    ACTIVATE_DEACTIVATE_CLIENT("Activate/Deactivate client" , UserType.ADMIN),

    //LICENSE TYPE
    CREATE_LICENSE_TYPE("Create license type" , UserType.ADMIN),
    VIEW_LICENSE_TYPE("View license type" , UserType.ADMIN),
    DELETE_LICENSE_TYPE("Delete license type" , UserType.ADMIN),
    DEACTIVATE_ACTIVATE_LICENSE_TYPE("Deactivate/Activate license type" , UserType.ADMIN),

    //PACKAGE TYPE
    CREATE_PACKAGE_TYPE("Create package type" , UserType.ADMIN),
    VIEW_PACKAGE_TYPE("View package type" , UserType.ADMIN),

    //PACKAGE
    CREATE_PACKAGE("Create package" , UserType.ADMIN),
    VIEW_PACKAGE("View package" , UserType.ADMIN),
    UPDATE_PACKAGE("Update package" , UserType.ADMIN),

    //ITEM
    CREATE_ITEM("Create item" , UserType.ADMIN),
    VIEW_ITEM("View item" , UserType.ADMIN),
    UPDATE_ITEM("Update item" , UserType.ADMIN),
    DEACTIVATE_ACTIVATE_ITEM("Deactivate/Activate item" , UserType.ADMIN),

    //PACKAGE RATE
    CREATE_PACKAGE_RATE("Create package rate" , UserType.ADMIN),
    VIEW_PACKAGE_RATE("View package rate" , UserType.ADMIN),

    //REGION RATE
    CREATE_REGION_RATE("Create region rate" , UserType.ADMIN),
    VIEW_REGION_RATE("View region rate" , UserType.ADMIN),

    //BILLING METHOD
    CREATE_BILLING_METHOD("Create billing method" , UserType.ADMIN),
    VIEW_BILLING_METHOD("View billing method" , UserType.ADMIN),
    DEACTIVATE_ACTIVATE_BILLING_METHOD("Deactivate/Activate billing method" , UserType.ADMIN),

    //BILL SETUP
    CREATE_BILL_SETUP("Create bill setup" , UserType.CUSTOMER),
    VIEW_BILL_SETUP("View bill setup" , List.of(UserType.CUSTOMER , UserType.ADMIN)),
    VIEW_BILL_SETUPS("View bill setup" , UserType.ADMIN),
    ACTIVATE_SUBSCRIPTION("Activate subscription" , UserType.CUSTOMER),

    //BILL LOG
    CREATE_BILL_LOG("Create bill log" , UserType.CUSTOMER),
    VIEW_BILL_LOG("View bill log" , List.of(UserType.CUSTOMER , UserType.ADMIN)),
    VIEW_BILL_LOGS("View bill log" , UserType.ADMIN),

    //LICENSE UPGRADE
    CREATE_LICENSE_UPGRADE("Create license upgrade" , UserType.CUSTOMER),
    VIEW_LICENSE_UPGRADE("View license upgrade" , List.of(UserType.CUSTOMER , UserType.ADMIN)),

    //AUDIT LOGS
    VIEW_AUDIT_LOGS("View audit logs" , List.of(UserType.ADMIN)),

    //NON FUNCTIONAL PERMISSIONS
    //ROLE
    CREATE_ROLE("Create role" , UserType.ADMIN),
    VIEW_ROLE("View role" , UserType.ADMIN),
    UPDATE_ROLE("Update role" , UserType.ADMIN),
    DISABLE_ENABLE_ROLE("Disable/Enable role" , UserType.ADMIN),

    //PERMISSION
    VIEW_ADMIN_PERMISSION("View tenant permission" , UserType.ADMIN),
    VIEW_CUSTOMER_PERMISSION("View System permission" , UserType.ADMIN);


    private final String description;
    private final List<UserType> userTypeList;

    PermissionName(String description , UserType userType) {
        this.description = description;
        this.userTypeList = Collections.singletonList(userType);
    }

    PermissionName(String description , List<UserType> admintypeList) {
        this.description = description;
        this.userTypeList = admintypeList;
    }

}
