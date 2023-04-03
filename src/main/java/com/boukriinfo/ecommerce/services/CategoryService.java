package com.boukriinfo.ecommerce.services;

import com.boukriinfo.ecommerce.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    Category saveCategory(Category category);
    List<Category> allCategories();
    Category getCategory(Long categoryId) ;
    Category updateCategory(Category category);

    Category updateDeletedCategory(Category category);
    boolean deleteCategory(Long categoryId);
    Page<Category> getAllCategoriesWithPage(int page, int size);
    Page<Category> getAllCategoriesWithSlugAndPage(String slug,int page, int size);

    List<Category> findAllCategoriesNotDeleted();
}
