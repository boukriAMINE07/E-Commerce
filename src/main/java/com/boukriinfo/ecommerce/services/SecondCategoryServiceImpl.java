package com.boukriinfo.ecommerce.services;

import com.boukriinfo.ecommerce.entities2.Category;
import com.boukriinfo.ecommerce.exceptions.CategoryNotFoundException;
import com.boukriinfo.ecommerce.repositories2.SecondCategoryRepository;
import org.springframework.beans.BeanUtils;
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
        category.setId(category.getId());
        Category savedCategory = secondCategoryRepository.save(category);
        return savedCategory;
    }

    @KafkaListener(topics = "topic-category", containerFactory = "secondKafkaListenerContainerFactory")
    public Category updateCategory(Category category) {
        Category categoryUpdate = secondCategoryRepository.findById(category.getId()).orElseThrow(() -> new CategoryNotFoundException("Category by Id :" + category.getId() + " not found in second category"));
        if (categoryUpdate != null) {
            categoryUpdate.setId(category.getId());
            categoryUpdate.setName(category.getName());
            categoryUpdate.setSlug(category.getSlug());
            categoryUpdate.setDescription(category.getDescription());
            categoryUpdate.setProducts(category.getProducts());
            categoryUpdate.setDeleted(category.isDeleted());
            categoryUpdate.setUpdatedAt(category.getUpdatedAt());
            categoryUpdate.setCreatedAt(category.getCreatedAt());
            secondCategoryRepository.save(categoryUpdate);

            return categoryUpdate;
        }
        return null;
    }

    @KafkaListener(topics = "topic-category", containerFactory = "secondKafkaListenerContainerFactory")
    public Category updateDeletedCategory(Category category) {
        Category categoryUpdate = secondCategoryRepository.findById(category.getId()).orElseThrow(() -> new CategoryNotFoundException("Category by Id :" + category.getId() + " not found in second category"));
        if (categoryUpdate != null) {
            secondCategoryRepository.save(categoryUpdate);
            return categoryUpdate;
        }
        return categoryUpdate;
    }
}
