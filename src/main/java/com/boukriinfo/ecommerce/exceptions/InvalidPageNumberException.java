package com.boukriinfo.ecommerce.exceptions;

public class InvalidPageNumberException  extends RuntimeException{
    public InvalidPageNumberException(String message) {
        super(message);
    }
}
