package com.boukriinfo.ecommerce.services;

import com.boukriinfo.ecommerce.entities2.Category;
import com.boukriinfo.ecommerce.repositories2.SecondCategoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SecondCategoryServiceImpl implements SecondCategoryService {

    private final SecondCategoryRepository secondCategoryRepository;
    private final KafkaTemplate<String, Category> kafkaTemplate;

    public SecondCategoryServiceImpl(SecondCategoryRepository secondCategoryRepository, KafkaTemplate<String, Category> kafkaTemplate) {
        this.secondCategoryRepository = secondCategoryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "topic-category", containerFactory = "secondKafkaListenerContainerFactory")
    public Category saveCategory(Category category) {
        Category savedCategory = secondCategoryRepository.save(category);
        return savedCategory;
    }
}
