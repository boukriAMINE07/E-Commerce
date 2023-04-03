package com.boukriinfo.ecommerce.exceptions;

public class WrongFieldTypeException extends RuntimeException{
    public WrongFieldTypeException(String message) {
        super(message);
    }
}
