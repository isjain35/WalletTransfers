package com.exercise.WalletTransfers.utils;

public class ResponseMessages {
    public static final String SUCCESSFUL_SIGNUP = "User signed up successfully";
    public static final String USER_ALREADY_EXISTS = "User is already signed up";
    public static final String LOGIN_SUCCESSFUL = "Successfully logged in";
    public static final String LOGIN_FAILED = "Either username or password is incorrect";
    public static final String INVALID_TOKEN = "Token you provided is invalid";
    public static final String WALLET_ALREADY_EXISTS = "Wallet already exists for user";
    public static final String WALLET_NOT_FOUND = "Wallet not found";
    public static final String TRANSFER_SUCCESSFUL = "Transfer completed successfully";
    public static final String TRANSFER_FAILED_RECIPIENT_WALLET_NOT_REGISTERED = "Recipient user or wallet not registered";
    public static final String TRANSFER_FAILED_SENDER_WALLET_NOT_REGISTERED = "Your wallet is not registered";
    public static final String TRANSFER_FAILED_INSUFFICIENT_BALANCE = "Insufficient balance";
    public static final String TRANSFER_NOT_FOUND = "Transfer not found";
    public static final String TRANSFER_NOT_UNIQUE = "Transfer unique_reference is already present";
}
