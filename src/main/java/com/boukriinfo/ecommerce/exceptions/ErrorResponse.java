package com.boukriinfo.ecommerce.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor @NoArgsConstructor @Data
public class ErrorResponse {
    private String message;
    private HttpStatus httpStatus;

}
