package com.boukriinfo.ecommerce.web;


import com.boukriinfo.ecommerce.entities.Category;
import com.boukriinfo.ecommerce.services.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin("*")
public class CategoryRestController {
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories(@RequestParam(required = false) String slug
                                                              , @RequestParam(name = "page",defaultValue = "0") int page,
                                                               @RequestParam(name = "size",defaultValue = "8") int size) {
        try {
            Page<Category> categoryPagePage;
            List<Category> categories = new ArrayList<Category>();
            if (slug == null || slug.isEmpty() ||  slug.trim().length()==0) {
                categoryPagePage = categoryService.getAllCategoriesWithPage(page, size);
            } else {
                categoryPagePage = categoryService.getAllCategoriesWithSlugAndPage(slug, page, size);
            }
            categories = categoryPagePage.getContent();
            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories);
            response.put("currentPage", categoryPagePage.getNumber());
            response.put("totalItems", categoryPagePage.getTotalElements());
            response.put("totalPages", categoryPagePage.getTotalPages());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);

        }
    }

    @GetMapping("/categories/all")
    public List<Category> categories() {
        return categoryService.allCategories();
    }
    @GetMapping("/categories/notDeleted")
    public List<Category> categoriesNotDeleted() {
        return categoryService.findAllCategoriesNotDeleted();
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategory(id);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @PostMapping("/categories")
    public ResponseEntity<Category> saveCategory(@RequestBody Category category) {
        try {
            Category category1 = categoryService.saveCategory(category);
            return ResponseEntity.ok(category1);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @PutMapping("/categories/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return categoryService.updateCategory(category);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(null);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/categories/{id}")
    public ResponseEntity<Category> patchCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            category.setId(id);
            Category category1 = categoryService.updateDeletedCategory(category);
            return ResponseEntity.ok(category1);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
