package com.boukriinfo.ecommerce;

import com.boukriinfo.ecommerce.entities.Category;
import com.boukriinfo.ecommerce.entities.ERole;
import com.boukriinfo.ecommerce.entities.Product;
import com.boukriinfo.ecommerce.entities.Role;
import com.boukriinfo.ecommerce.repositories.CategoryRepository;
import com.boukriinfo.ecommerce.repositories.ProductRepository;

import com.boukriinfo.ecommerce.repositories.RoleRepository;
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

    @Bean
    CommandLineRunner saveCategory(CategoryRepository categoryRepository){

        return args -> {
            Faker faker = new Faker();
            for (int i = 0; i < 10; i++) {
                Category category = new Category();
                category.setName(faker.commerce().department());
                category.setSlug(faker.commerce().department());
                categoryRepository.save(category);
            }
        };
    }
    @Bean
    CommandLineRunner saveRole(RoleRepository roleRepository){

            return args -> {
                Role role = new Role();
                role.setName(ERole.ROLE_USER);
                roleRepository.save(role);
                role.setName(ERole.ROLE_ADMIN);
                roleRepository.save(role);
            };
    }






}
