package com.boukriinfo.ecommerce.repositories2;

import com.boukriinfo.ecommerce.entities2.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecondCategoryRepository extends JpaRepository<Category,Long>{

}
