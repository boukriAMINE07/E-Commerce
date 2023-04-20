package com.boukriinfo.ecommerce;

import com.boukriinfo.ecommerce.entities.Category;
import com.boukriinfo.ecommerce.entities.Product;
import com.boukriinfo.ecommerce.repositories.CategoryRepository;
import com.boukriinfo.ecommerce.repositories.ProductRepository;
import com.boukriinfo.ecommerce.repositories2.SecondCategoryRepository;
import com.boukriinfo.ecommerce.repositories2.SecondProductRepositories;
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



    //@Bean
    CommandLineRunner saveCategoriesAndCategories2WithFaker(CategoryRepository categoryRepository,SecondCategoryRepository secondCategoryRepository) {
        return args -> {
            Faker faker=new Faker(); // Créez une instance de la classe Faker

            final int NUM_CATEGORIES = 10; // Définissez le nombre de noms de catégories à générer

            for (int i = 0; i < NUM_CATEGORIES; i++) {
                String name = faker.commerce().productName();
                String description= faker.lorem().sentence();
                Date createdAt = faker.date().birthday();
                boolean deleted = Math.random() > 0.5;

                Category category = new Category();
                com.boukriinfo.ecommerce.entities2.Category category2 = new com.boukriinfo.ecommerce.entities2.Category();


                category.setName(name);
                category2.setName(name);
                category.setSlug(name.toLowerCase().replace(" ", "-"));
                category2.setSlug(name.toLowerCase().replace(" ", "-"));

                category.setDescription(description);
                category2.setDescription(description);
                category.setCreatedAt(createdAt);
                category2.setCreatedAt(createdAt);
                //category.setUpdatedAt(faker.date().birthday());
                category.setDeleted(deleted);
                category2.setDeleted(deleted);

                categoryRepository.save(category);
                secondCategoryRepository.save(category2);
            }
        };
    }





    //@Bean
    CommandLineRunner saveProducts(ProductRepository productRepository, CategoryRepository categoryRepository) {
        return args -> {
            List<Category> categories = categoryRepository.findAll();
            Faker faker = new Faker();

            categories.forEach(category -> {
                for (int i = 1; i <= 5; i++) {
                    Product product = new Product();
                    product.setCategory(category);
                    product.setName(faker.commerce().productName());
                    product.setSlug((category.getSlug() + "-produit-" + i).toLowerCase());
                    product.setDescription(faker.lorem().sentence(10));
                    product.setPrice(faker.number().randomDouble(2, 100, 1000));
                    product.setImage("product.jpg");
                    product.setCreatedAt(new Date());
                    //product.setUpdatedAt(new Date());
                    product.setDeleted(faker.bool().bool());
                    productRepository.save(product);
                }
            });
        };
    }
   //
   //@Bean
    CommandLineRunner saveProducts2(ProductRepository productRepository,SecondProductRepositories secondProductRepositories, SecondCategoryRepository secondCategoryRepository) {
        return args -> {
            List<Product> products = productRepository.findAll();

            products.forEach(product -> {
                    com.boukriinfo.ecommerce.entities2.Product product2 = new com.boukriinfo.ecommerce.entities2.Product();
                    com.boukriinfo.ecommerce.entities2.Category category2 = new com.boukriinfo.ecommerce.entities2.Category();
                    BeanUtils.copyProperties(product.getCategory(), category2);
                    product2.setCategory(category2);
                    product2.setName(product.getName());
                    product2.setSlug(product.getSlug());
                    product2.setDescription(product.getDescription());
                    product2.setPrice(product.getPrice());
                    product2.setImage(product.getImage());
                    product2.setCreatedAt(product.getCreatedAt());
                    //product2.setUpdatedAt(product.getUpdatedAt());
                    product2.setDeleted(product.getDeleted());
                    secondProductRepositories.save(product2);

            });

        };
    }


}
