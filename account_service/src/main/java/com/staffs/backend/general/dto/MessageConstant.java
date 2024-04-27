package com.staffs.backend.general.dto;

public class MessageConstant {

    private MessageConstant() {
    }

    public static final String SUCCESS = "Successful!";
    public static final String RECORD_NOT_FOUND = "Record not found!";
    public static final String INVALID_ACTION = "Invalid action!";
    public static final String PAYMENT_FAILED = "Payment failed!";
    public static final String INVALID_TRANSACTION = "Invalid transaction";
    public static final String INVALID_PACKAGE_NAME = "Invalid package name!";
    public static final String LICENSE_UPGRADE_ERROR = "license upgrade error!";
    public static final String RECORD_ALREADY_EXISTS = "Record already exists!";
    public static final String WRONG_PARAMETER_PARSE = "Wrong parameter parse!";
    public static final String INVALID_BILLING_METHOD = "invalid billing method!";
    public static final String INVALID_REQUEST_PARAMS = "Invalid Request Params!";
    public static final String ITEM_IS_NOT_STANDALONE = "Item is not standalone!";
    public static final String INVALID_PAYMENT_REQUEST = "Invalid payment request";
    public static final String INVALID_PAYMENT_SOURCE = "Invalid payment source parsed!";
    public static final String ITEM_ALREADY_ATTACHED = "Item already attached to package!";
    public static final String VERSION_NUMBER_ALREADY_EXISTS = "version number already exists!";
    public static final String BILL_RATE_NOT_FOUND_FOR_PACKAGE = "Bill rate not found for package!";
    public static final String INVALID_LICENSE_UPGRADE_PACKAGE = "Invalid license upgrade package!";
    public static final String INVALID_LICENSE_UPGRADE_FROM_INFO = "Invalid license upgrade from info!";
    public static final String NO_ACTIVE_BILL_SETUP_FOUND = "No active bill setup found for this account!";
    public static final String FIRSTNAME_CAN_NOT_BE_NULL_NOR_EMPTY = "Firstname can not be null nor empty!";
    public static final String ITEM_QUANTITY_CAN_NOT_BE_LESS_THAN_1 = "Item Quantity can not be less than 1!";
    public static final String BILL_AMOUNT_IS_NOT_SAME_AS_PAID_AMOUNT = "Bill amount is not the same as paid amount!";
    public static final String CHARGE_AMOUNT_IS_NOT_SAME_AS_PAID_AMOUNT = "Charge amount is not same as paid amount!";
    public static final String ITEM_CANNOT_BE_DETACHED = "Item cannot be detach from package that is not attached to!";
    public static final String NO_COUPON_LEFT_FOR_THIS_SUBSCRIPTION_ITEM = "No coupon left for this subscription item!";
    public static final String EMAIL_OR_PHONE_NUMBER_MUST_BE_PROVIDED = "Either phone number or email must be provided!";
    public static final String PHONE_NUMBER_NEED_TO_BE_PROVIDED_FOR_MPESA = "Phone number need to be provided for MPESA!";
    public static final String BILL_PRICE_CANNOT_BE_GREATER_LESS_THAN_CAP_MIN_PRICE = "Bill price cannot be greater/less than cap/min price!";
    public static final String PHONE_NUMBER_NEED_TO_BE_KENYA_WITH_254_COUNTRY_CODE = "Phone number need to be Kenya's with 254 country code!";
    public static final String UPGRADE_PACKAGE_TO_HAS_TO_BE_OF_SAME_LICENSE_WITH_UPGRADE_PACKAGE_FROM = "upgrade package to has to be of same license with upgrade package from!";
    public static final String ACCOUNT_ALREADY_HAS_A_VALID_BILL_SETUP_ON_THIS_LICENSE_TYPE_AND_PACKAGE = "Account already has a valid bill setup on this License Type and Package!";
    public static final String PACKAGE_REGION_RATE_FOR_COUNTRY_KENYA_IS_NEEDED_TO_BE_ABLE_TO_PAY_THROUGH_THIS_SOURCE = "Package region rate for Country Kenya is needed to be able to pay through this source";

}