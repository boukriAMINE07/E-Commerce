package com.boukriinfo.ecommerce.services;

import com.boukriinfo.ecommerce.entities2.Category;
import com.boukriinfo.ecommerce.entities2.Product;
import com.boukriinfo.ecommerce.repositories2.SecondProductRepositories;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service

public class SecondProductServiceImpl implements SecondProductService {
    private final SecondProductRepositories secondProductRepositories;
    private final KafkaTemplate<String, Product> kafkaTemplate;

    public SecondProductServiceImpl(SecondProductRepositories secondProductRepositories, KafkaTemplate<String, Product> kafkaTemplate) {
        this.secondProductRepositories = secondProductRepositories;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "topic-product", containerFactory = "productKafkaListenerContainerFactory")
    public Product saveProduct(Product product) {
        Product savedProduct = secondProductRepositories.save(product);
        return savedProduct;
    }
}
