package com.boukriinfo.ecommerce.services;

import com.boukriinfo.ecommerce.entities.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Product saveProduct(Product product);
    List<Product> allProducts();
    Product getProduct(Long productId) ;
    Product updateProduct(Product product);
    boolean deleteProduct(Long productId);
    Page<Product> getAllProductsWithPage(int page, int size);
    Page<Product> getAllProductsWithSlugAndPage(String slug,int page, int size);

    List<Product> findAllProductsNotDeleted();

    Product updateDeletedProduct(Product product);
}
