package com.example.store.repository;

import com.example.store.model.Category;
import com.example.store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByPriceBetween(double minPrice, double maxPrice);
    List<Product> findByCategoryAndPriceBetween(String category, double minPrice, double maxPrice);

}
