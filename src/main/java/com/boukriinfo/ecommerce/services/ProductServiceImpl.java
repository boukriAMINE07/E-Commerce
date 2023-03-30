package com.boukriinfo.ecommerce.services;

import com.boukriinfo.ecommerce.entities.Product;
import com.boukriinfo.ecommerce.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> allProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product by Id :"+productId+" not found"));
        return product;
    }

    @Override
    public Product updateProduct(Product product) {
        Product productUpdate = productRepository.findById(product.getId()).orElseThrow(() -> new RuntimeException("Product by Id :"+product.getId()+" not found"));
        if (productUpdate != null){
            BeanUtils.copyProperties(product,productUpdate);
            productRepository.save(productUpdate);
            return productUpdate;
        }
        return null;
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);

    }

    @Override
    public Page<Product> getAllProductsWithPage(int page, int size) {
        return productRepository.findAllNotDeleted(PageRequest.of(page,size));
    }

    @Override
    public Page<Product> getAllProductsWithSlugAndPage(String slug,int page, int size) {
        return productRepository.findAllBySlugContainingAndNotDeleted(slug,PageRequest.of(page,size));
    }

    @Override
    public List<Product> findAllProductsNotDeleted() {
        return productRepository.findAllProductsNotDeleted();
    }
}
