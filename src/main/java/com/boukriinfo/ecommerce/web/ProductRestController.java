package com.boukriinfo.ecommerce.web;

import com.boukriinfo.ecommerce.entities.Product;
import com.boukriinfo.ecommerce.exceptions.*;
import com.boukriinfo.ecommerce.repositories.ProductRepository;
import com.boukriinfo.ecommerce.services.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;



@RestController
@Slf4j
@CrossOrigin("*")
@RequestMapping("/api/")
public class ProductRestController {
    @Autowired

    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;


    private String pathURL="";
    private String name="";
    @GetMapping("products/all")
    @PreAuthorize("hasRole('ADMIN') ")
    public List<Product> products() {
        return productRepository.findAll();
    }


    @GetMapping("products")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<Map<String, Object>> getProducts(@RequestParam(required = false) String slug,
                                                              @RequestParam(name = "page",defaultValue = "0") String pageStr,
                                                               @RequestParam(name = "size",defaultValue = "6") String sizeStr) {
        try {
            int page = validatePositiveIntegerParameter(pageStr, "La page doit être un nombre entier positif");
            int size = validatePositiveIntegerParameter(sizeStr, "size doit être un nombre entier positif");
            Page<Product> productsPage;
            List<Product> products = new ArrayList<Product>();
            if (slug == null || slug.isEmpty()) {
                productsPage = productService.getAllProductsWithPage(page, size);
            } else {
                productsPage = productService.getAllProductsWithSlugAndPage(slug,page, size);
            }
            products = productsPage.getContent();
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("currentPage", productsPage.getNumber());
            response.put("totalItems", productsPage.getTotalElements());
            response.put("totalPages", productsPage.getTotalPages());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e) {
            // renvoyer une réponse BadRequest avec le message personnalisé
            String errorMessage = e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", errorMessage));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(null);

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


    @GetMapping("products/{id}")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<Object> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.getProduct(id);
            return ResponseEntity.ok(product);
        } catch (ProductNotFoundException e) {
            ErrorResponse error = new ErrorResponse("Product not found with id " + id, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(error, error.getHttpStatus());
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Error occurred while fetching product with id " + id, HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(error, error.getHttpStatus());
        }
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid "+ex.getName().toUpperCase()+" type. Expected type: " + ex.getRequiredType().getSimpleName();
        ErrorResponse error = new ErrorResponse(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, error.getHttpStatus());
    }





    @PostMapping(value = "products/upload" , consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        log.info("uploadImage"+file.getOriginalFilename());
        try {
            // Vérifie que le fichier est bien une image
            if (!file.getContentType().startsWith("image")) {
                return ResponseEntity.badRequest().body("Le fichier n'est pas une image");
            }

            // Génère un nom de fichier unique
            String fileName = UUID.randomUUID().toString() + "." + getFileExtension(file.getOriginalFilename());
            log.info("fileName: " + fileName);

            name=fileName;
            // Enregistre le fichier sur le disque
            Path path = Paths.get("C:\\DATA_AMINE\\projects\\e-commerce_ui\\public\\assets\\assets\\img\\" + fileName);
            log.info("path: " + path);
            this.pathURL=path.toString();
            Files.write(path, file.getBytes());

            return ResponseEntity.ok(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de l'enregistrement du fichier");
        }
    }


    private String getFileExtension(String fileName) {
        String[] parts = fileName.split("\\.");
        if (parts.length > 1) {
            return parts[parts.length - 1];
        }
        return "";
    }

    @PostMapping(value = "products" , consumes = { "application/json"})
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<?> saveProduct(@Valid @RequestBody Product product, BindingResult result)  {
        ResponseEntity<?> responseEntity = handleValidationErrors(result);
        if (responseEntity != null) {
            return responseEntity;
        }
        product.setImage(this.name);
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(savedProduct);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = ex.getMessage();
        if (errorMessage.contains("Cannot deserialize value of type `double` from String")) {
            errorMessage = "price doit etre un nombre entier positif";
        } else if(errorMessage.contains("Cannot deserialize value of type `boolean` from String")) {
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


    @GetMapping ("products/notDeleted")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<List<Product>> findAllProductsNotDeleted() {
        try {
            List<Product> products = productService.findAllProductsNotDeleted();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }



    @PutMapping("products/{id}")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<?> updateProduct( @PathVariable Long id,@Valid @RequestBody Product product, BindingResult result) {
        product.setId(id);
        ResponseEntity<?> responseEntity = handleValidationErrors(result);
        if (responseEntity != null) {
            return responseEntity;
        }

        if (this.name!=""){
            product.setImage(this.name);
        }
        Product product1 = productService.updateProduct(product);
        return ResponseEntity.ok(product1);
    }

    @DeleteMapping("products/{id}")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            boolean success = productService.deleteProduct(id);
           if (success) {
                return ResponseEntity.ok(new ErrorResponse("Le Produit a été supprimée avec succès .", HttpStatus.OK));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting product with id " + id);

        } catch (ProductNotFoundException e) {
            ErrorResponse error = new ErrorResponse("Product not found with id " + id, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(error, error.getHttpStatus());
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Error occurred while deleting product with id " + id, HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(error, error.getHttpStatus());
        }
    }

    @PatchMapping("products/{id}")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, @RequestBody Product product) {
        try{
            product.setId(id);
            Product product1 = productService.updateDeletedProduct(product);

            return ResponseEntity.ok(product1);
        } catch (CategoryNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Product by Id :" + id + " not found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Une erreur s'est produite lors de la mise à jour de Produit : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
