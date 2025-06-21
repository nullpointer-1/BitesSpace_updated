// src/main/java/com/example/shopmanagement/repository/ProductRepository.java
package com.example.shopmanagement.repository;

import com.example.shopmanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query method to find products associated with a specific shop
    // Spring Data JPA automatically derives the query from the method name
    List<Product> findByShop_Id(Long shopId);
}