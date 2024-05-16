package com.enigma.laternak.constant;

import lombok.Getter;

@Getter
public enum Message {
    // Error Message
    ERROR_INTERNAL_SERVER_ERROR("Internal Server Error"),

    ERROR_PRODUCT_NOT_FOUND("Product Not Found"),
    ERROR_STORE_NOT_FOUND("Store Not Found"),
    ERROR_USER_NOT_FOUND("User Not Found"),
    ERROR_CART_NOT_FOUND("Cart Not Found"),
    ERROR_IMAGE_NOT_FOUND("Image Not Found"),
    ERROR_ORDER_NOT_FOUND("Order Not Found"),
    ERROR_REVIEW_NOT_FOUND("Review Not Found"),
    ERROR_EMAIL_NOT_FOUND("Email Not Found"),

    ERROR_ACCOUNT_NOT_ACTIVE("Account is not active"),
    ERROR_ACCOUNT_NOT_SELLER("Account is not a seller"),
    ERROR_ACCOUNT_NOT_CUSTOMER("Account is not a customer"),
    ERROR_UNABLE_SEND_EMAIL("Unable to send email"),

    ERROR_IMAGE_NULL("Image is required"),
    ERROR_IMAGE_CONTENT_TYPE("Image must be image"),

    ERROR_OUT_OF_STOCK("Out of stock"),
    ERROR_INVALID_JWT_TOKEN("Invalid JWT token"),

    // Success Message
    SUCCESS_GET_DATA("Get data successfully"),
    SUCCESS_GET_ALL_DATA("Get all data successfully"),

    SUCCESS_CREATE("Create data successfully"),
    SUCCESS_CREATE_ACCOUNT("Create new account successfully"),
    SUCCESS_CREATE_PRODUCT("Create new product successfully"),
    SUCCESS_CREATE_ORDER("Create new order successfully"),
    SUCCESS_CREATE_REVIEW("Create new review successfully"),

    SUCCESS_LOGIN("Login successfully"),
    SUCCESS_REGISTER("Register successfully"),

    SUCCESS_UPDATE("Update data successfully"),
    SUCCESS_UPDATE_ORDER_STATUS("Update order status successfully"),

    SUCCESS_DELETE("Delete data successfully"),
    SUCCESS_ADD_TO_CART("Add to cart successfully"),
    SUCCESS_VALID_TOKEN("Token is valid"),
    SUCCESS_REGENERATE_OTP("Regenerate OTP successfully"),
    SUCCESS_VERIFY_ACCOUNT("Verify account successfully"),
    SUCCESS_UNACTIVATED_ACCOUNT_SELLER("Unactivated account seller successfully"),
    SUCCESS_UNACTIVATED_ACCOUNT_CUSTOMER("Unactivated account user successfully")

    ;

    private final String message;

    Message(String message) {
        this.message = message;
    }
}
