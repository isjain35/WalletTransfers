package com.exercise.WalletTransfers.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {
    private HttpStatus httpStatus;
    private Object responseObject;
    public ResponseEntity<Object> getResponseEntity() {
        return new ResponseEntity<>(responseObject, httpStatus);
    }
}
