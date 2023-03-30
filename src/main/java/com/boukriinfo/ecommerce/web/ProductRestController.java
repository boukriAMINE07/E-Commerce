package com.boukriinfo.ecommerce.web;

import com.boukriinfo.ecommerce.entities.Product;
import com.boukriinfo.ecommerce.repositories.ProductRepository;
import com.boukriinfo.ecommerce.services.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@RestController

@Slf4j
@CrossOrigin("*")
public class ProductRestController {
    @Autowired

    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    private String pathURL="";
    private String name="";
    @GetMapping("/products/all")
    public List<Product> products() {
        return productRepository.findAll();
    }


    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts(@RequestParam(required = false) String slug,
                                                              @RequestParam(name = "page",defaultValue = "0") int page,
                                                               @RequestParam(name = "size",defaultValue = "6") int size) {
        try {
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
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);

        }
    }


    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.getProduct(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


   @PostMapping(value = "/products/upload" , consumes = {"multipart/form-data"})
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

    @PostMapping(value = "/products" , consumes = { "application/json"})
    public Product saveProduct( @RequestBody Product product) throws IOException {
           // String fileName = UUID.randomUUID().toString() + ".jpg";
            //log.info("data type of categorie id" + product);
            // Enregistre le fichier sur le disque
            //Path path = Paths.get("C:\\DATA_AMINE\\projects\\e-commerce_ui\\public\\assets\\assets\\img\\" + name);
           // byte[] bytes = this.pathURL.getBytes();
            //Files.write(path, bytes);
            //String encodedString = Base64.getEncoder().encodeToString(bytes);
            product.setImage(this.name);

             //Enregistre le produit dans la base de données
           Product savedProduct = productService.saveProduct(product);

            // Récupère le chemin du fichier image
            String imagePath = "C:\\DATA_AMINE\\projects\\e-commerce_ui\\public\\assets\\assets\\img\\" + savedProduct.getImage();

            // Remplace le contenu du fichier par la version encodée en Base64
            savedProduct.setImage(imagePath);

            return savedProduct;

    }



    @GetMapping ("/products/notDeleted")
    public ResponseEntity<List<Product>> findAllProductsNotDeleted() {
        try {
            List<Product> products = productService.findAllProductsNotDeleted();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }



    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        if (this.name!=""){
            product.setImage(this.name);
        }
        return productService.updateProduct(product);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @PatchMapping("/products/{id}")
    public Product deleteProduct(@PathVariable Long id, @RequestBody Product product) {
        Product productFromDb = productService.getProduct(id);
        if (productFromDb != null) {
            if (product.getDeleted()==false ) {
                productFromDb.setDeleted(true);
            }

            return productService.updateProduct(productFromDb);
        }
        return null;
    }



}
