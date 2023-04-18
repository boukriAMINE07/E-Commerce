package com.boukriinfo.ecommerce.services;

import com.boukriinfo.ecommerce.entities2.Category;

public interface SecondCategoryService {
    Category saveCategory(Category category);
    Category updateCategory(Category category);
    Category updateDeletedCategory(Category category);
}
