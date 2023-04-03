package com.boukriinfo.ecommerce;

import com.boukriinfo.ecommerce.entities.Category;
import com.boukriinfo.ecommerce.entities.Product;
import com.boukriinfo.ecommerce.repositories.CategoryRepository;
import com.boukriinfo.ecommerce.repositories.ProductRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class ECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }
   //@Bean
    CommandLineRunner saveCategories(CategoryRepository categoryRepository){
        return args -> {
            String[] categoryNames = {"Ordinateurs portables", "Smartphones", "Appareils photo", "Accessoires pour ordinateurs", "Accessoires pour smartphones"};

            for (String name : categoryNames) {
                Category category = new Category();
                category.setName(name);
                category.setSlug(name.toLowerCase().replace(" ", "-"));
                category.setDescription("Description pour la catégorie " + name);
                category.setCreatedAt(new Date());
                category.setUpdatedAt(new Date());
                category.setDeleted(Math.random()>0.5?true:false);
                categoryRepository.save(category);
            }

        };
    }

    //@Bean
    CommandLineRunner saveCategoriesWithFaker(CategoryRepository categoryRepository) {
        return args -> {
            Faker faker=new Faker(); // Créez une instance de la classe Faker

            final int NUM_CATEGORIES = 20; // Définissez le nombre de noms de catégories à générer

            for (int i = 0; i < NUM_CATEGORIES; i++) {
                String name = faker.commerce().productName();

                Category category = new Category();
                category.setName(name);
                category.setSlug(name.toLowerCase().replace(" ", "-"));
                category.setDescription(faker.lorem().sentence());
                category.setCreatedAt(faker.date().birthday());
                category.setUpdatedAt(faker.date().birthday());
                category.setDeleted(Math.random() > 0.5);
                categoryRepository.save(category);
            }
        };
    }




   // @Bean
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
                    product.setImage(null);
                    product.setCreatedAt(new Date());
                    product.setUpdatedAt(new Date());
                    product.setDeleted(faker.bool().bool());
                    productRepository.save(product);
                }
            });
        };
    }


}
