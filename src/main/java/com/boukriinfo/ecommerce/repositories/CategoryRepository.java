package com.boukriinfo.ecommerce.repositories;

import com.boukriinfo.ecommerce.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long>{


    @Query(value = "SELECT c FROM Category c WHERE c.slug LIKE %:slug% AND c.deleted = false ORDER BY c.id DESC")
    Page<Category> findAllBySlugContainingAndNotDeleted(@Param("slug") String slug, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.deleted = false ORDER BY c.id DESC")
    Page<Category> findAllNotDeleted(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.deleted = false ORDER BY c.id DESC")
    List<Category> findAllProductsNotDeleted();

}
