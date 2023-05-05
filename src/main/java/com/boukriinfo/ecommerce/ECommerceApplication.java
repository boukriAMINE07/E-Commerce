package com.boukriinfo.ecommerce;

import com.boukriinfo.ecommerce.entities.Category;
import com.boukriinfo.ecommerce.entities.Product;
import com.boukriinfo.ecommerce.repositories.CategoryRepository;
import com.boukriinfo.ecommerce.repositories.ProductRepository;

import com.github.javafaker.Faker;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import java.util.Date;
import java.util.List;


@SpringBootApplication
public class ECommerceApplication {



    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }






}
