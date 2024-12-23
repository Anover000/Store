package com.example.store.controller;

import com.example.store.dto.ProductCreationRequestDTO;
import com.example.store.dto.ProductResponseDTO;
import com.example.store.dto.ProductUpdationRequestDTO;
import com.example.store.model.Category;
import com.example.store.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponseDTO> getProducts(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        return productService.getProductsByFilter(category, minPrice, maxPrice);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping
    //@CacheEvict(value = {"products", "productsList"}, allEntries = true) // Clear cache
    public ProductResponseDTO addProduct(@RequestBody @Valid ProductCreationRequestDTO product) {
        return productService.addProduct(product);
    }

    @PutMapping("/{id}")
    //@CacheEvict(value = {"products", "productsList"}, allEntries = true) // Clear cache
    public ProductResponseDTO updateProduct(@PathVariable String id, @RequestBody ProductUpdationRequestDTO product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    //@CacheEvict(value = {"products", "productsList"}, allEntries = true) // Clear cache
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }
}
