package com.boukriinfo.ecommerce.exceptions;

public class CategoryNotFoundException  extends RuntimeException{
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
