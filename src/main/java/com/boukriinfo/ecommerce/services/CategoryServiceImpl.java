package com.boukriinfo.ecommerce.services;

import com.boukriinfo.ecommerce.entities.Category;
import com.boukriinfo.ecommerce.exceptions.CategoryNotFoundException;
import com.boukriinfo.ecommerce.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> allCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category by Id :" + categoryId + " not found"));
        return category;
    }

    @Override
    public Category updateCategory(Category category) {
        Category categoryUpdate = categoryRepository.findById(category.getId()).orElseThrow(() -> new CategoryNotFoundException("Category by Id :" + category.getId() + " not found"));
        if (categoryUpdate != null) {
            categoryUpdate.setName(category.getName());
            categoryUpdate.setSlug(category.getSlug());
            categoryUpdate.setDescription(category.getDescription());
            categoryUpdate.setProducts(category.getProducts());
            categoryUpdate.setDeleted(category.isDeleted());
            categoryUpdate.setUpdatedAt(category.getUpdatedAt());
            categoryUpdate.setCreatedAt(category.getCreatedAt());
            categoryRepository.save(categoryUpdate);
            return categoryUpdate;
        }
        return null;
    }

    @Override
    public Category updateDeletedCategory(Category category) {
        Category categoryUpdate = categoryRepository.findById(category.getId()).orElseThrow(() -> new CategoryNotFoundException("Category by Id :" + category.getId() + " not found"));
        if (categoryUpdate != null) {
            categoryUpdate.setDeleted(true);
            categoryRepository.save(categoryUpdate);
            return categoryUpdate;
        }
        return null;
    }

    @Override
    public boolean deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("Category by Id :" + categoryId + " not found"));
        if (category != null) {
            categoryRepository.deleteById(categoryId);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public Page<Category> getAllCategoriesWithPage(int page, int size) {
        return categoryRepository.findAllNotDeleted(PageRequest.of(page, size));
    }

    @Override
    public Page<Category> getAllCategoriesWithSlugAndPage(String slug, int page, int size) {
        return categoryRepository.findAllBySlugContainingAndNotDeleted(slug, PageRequest.of(page, size));
    }

    @Override
    public List<Category> findAllCategoriesNotDeleted() {
        List<Category> categories = categoryRepository.findAllProductsNotDeleted();
        if (categories.isEmpty()) {
            throw new CategoryNotFoundException("Aucune catégorie non supprimée n'a été trouvée.");
        }
        return categories;
    }

}
