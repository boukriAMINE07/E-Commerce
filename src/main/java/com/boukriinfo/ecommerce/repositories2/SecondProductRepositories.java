package com.boukriinfo.ecommerce.repositories2;

import com.boukriinfo.ecommerce.entities2.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecondProductRepositories extends JpaRepository<Product,Long> {
}
