package com.boukriinfo.ecommerce.web;


import com.boukriinfo.ecommerce.entities.Category;
import com.boukriinfo.ecommerce.exceptions.*;
import com.boukriinfo.ecommerce.services.CategoryService;
import com.github.javafaker.Bool;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/")
public class CategoryRestController {
    private CategoryService categoryService;

    @GetMapping("categories")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<Map<String, Object>> getCategories(@RequestParam(required = false) String slug
                                                              , @RequestParam(name = "page",defaultValue = "0") String pageStr,
                                                               @RequestParam(name = "size",defaultValue = "8") String sizeStr) {
        try {
            int page = validatePositiveIntegerParameter(pageStr, "La page doit être un nombre entier positif");
            int size = validatePositiveIntegerParameter(sizeStr, "size doit être un nombre entier positif");


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
        }catch (IllegalArgumentException e) {
            // renvoyer une réponse BadRequest avec le message personnalisé
            String errorMessage = e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", errorMessage));
        }
        catch (Exception e) {
            String errorMessage = "Une erreur s'est produite lors de la récupération des catégories";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", errorMessage));
        }

    }
    private int validatePositiveIntegerParameter(String parameter, String errorMessage) {
        if (!parameter.matches("\\d+")) {
            throw new IllegalArgumentException(errorMessage);
        } else {
            int intValue = Integer.parseInt(parameter);
            if (intValue < 0) {
                throw new IllegalArgumentException(errorMessage);
            }
            return intValue;
        }
    }

    @GetMapping("/categories/all")
    @PreAuthorize("hasRole('ADMIN') ")
    public List<Category> categories() {
        return categoryService.allCategories();
    }

    @GetMapping("/categories/notDeleted")
    @PreAuthorize("hasRole('ADMIN') ")
    public List<Category> categoriesNotDeleted() {
        try {
            return categoryService.findAllCategoriesNotDeleted();
        } catch (CategoryNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }


    @GetMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<Object> getCategory(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategory(id);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            String errorMessage = "Une erreur s'est produite lors de la récupération de la catégorie avec l'id " + id + ": " + e.getMessage();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CategoryError(errorMessage));
        }
    }
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveCategory(@Valid @RequestBody Category category, BindingResult result) {
        ResponseEntity<?> responseEntity = handleValidationErrors(result);
        if (responseEntity != null) {
            return responseEntity;
        }

        try {
            // Check for wrong field type
            if (!isValidFieldType(category)) {
                throw new CategoryException("Invalid field type for category");
            }
            Category category1 = categoryService.saveCategory(category);
            return ResponseEntity.ok(category1);
        } catch (CategoryException wfe) {
            String errorMessage = "Une erreur s'est produite lors de la sauvegarde de la catégorie : " + wfe.getMessage();
            log.error(errorMessage, wfe);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CategoryError(errorMessage));
        } catch (HttpMessageNotReadableException e) {
            String errorMessage = "Une erreur s'est produite lors de la désérialisation de la requête : " + e.getMessage();
            log.error(errorMessage, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CategoryError(errorMessage));
        } catch (Exception e) {
            String errorMessage = "Une erreur s'est produite lors de la sauvegarde de la catégorie : " + e.getMessage();
            log.error(errorMessage, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CategoryError(errorMessage));
        }
    }
    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category, BindingResult result) {
        // Check for validation errors
        ResponseEntity<?> responseEntity = handleValidationErrors(result);
        if (responseEntity != null) {
            return responseEntity;
        }
        try {
            // Check for wrong field type
            if (!isValidFieldType(category)) {
                    throw new CategoryException("Invalid field type for category");
            }
            category.setId(id);
            Category updatedCategory = categoryService.updateCategory(category);
            return ResponseEntity.ok(updatedCategory);
        } catch (CategoryNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Category by Id :" + id + " not found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (CategoryException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Une erreur s'est produite lors de la mise à jour de la catégorie : " + ex.getMessage(), HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Une erreur s'est produite lors de la mise à jour de la catégorie", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = ex.getMessage();
        if(errorMessage.contains("Cannot deserialize value of type `boolean` from String")) {
            errorMessage = "Le champ 'deleted' doit être un boolean 'true' Or 'false '.";
        }
        else{
            errorMessage = "Une erreur s'est produite lors de la désérialisation de la requête : " + ex.getMessage();
        }
        log.error(errorMessage, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST));
    }

    private ResponseEntity<?> handleValidationErrors(BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        return null;
    }

    private boolean isValidFieldType(Category category) {
        Class<? extends Category> clazz = category.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            Object value = null;
            try {
                field.setAccessible(true);
                value = field.get(category);
            } catch (IllegalAccessException e) {
                log.error("Error getting value of field " + field.getName(), e);
                return false;
            }

            if (value != null && !(value instanceof String || value instanceof Date || value instanceof Boolean)) {
                log.debug(field.getName() + " is not a valid field type");
                return false;
            }
        }

        return true;
    }


    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            boolean success = categoryService.deleteCategory(id);
            if (success) {
                return ResponseEntity.ok( new ErrorResponse("La catégorie a été supprimée avec succès.", HttpStatus.OK));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Category by Id : " + id + " not found", HttpStatus.NOT_FOUND));
            }

        }catch (CategoryNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Category by Id :" + id + " not found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Une erreur s'est produite lors de la suppression de la catégorie avec id : "+id, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }



    @PatchMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<?> patchCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            category.setId(id);
            Category category1 = categoryService.updateDeletedCategory(category);
            return ResponseEntity.ok(category1);
        } catch (CategoryNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Category by Id :" + id + " not found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Une erreur s'est produite lors de la mise à jour de la catégorie : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
