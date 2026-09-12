package com.exercise.WalletTransfers.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class WalletException extends RuntimeException{
    private HttpStatus httpStatus;
    private Object responseObject;
}
