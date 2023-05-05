package com.boukriinfo.ecommerce.repositories;

import com.boukriinfo.ecommerce.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long>{

    @Query("SELECT p FROM Product p WHERE p.slug LIKE %:slug% AND p.deleted = false ORDER BY p.id DESC")
    Page<Product> findAllBySlugContainingAndNotDeleted(@Param("slug") String slug, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.deleted = false ORDER BY p.id DESC")
    Page<Product> findAllNotDeleted(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deleted = false ORDER BY p.id DESC")
    List<Product> findAllProductsNotDeleted();




}
