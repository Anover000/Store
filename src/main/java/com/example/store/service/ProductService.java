package com.example.store.service;

import com.example.store.dto.ProductCreationRequestDTO;
import com.example.store.dto.ProductResponseDTO;
import com.example.store.dto.ProductUpdationRequestDTO;
import com.example.store.model.Category;

import java.util.List;

public interface ProductService {

    public List<ProductResponseDTO> getAllProducts();

    public ProductResponseDTO getProductById(String id);

    public ProductResponseDTO addProduct(ProductCreationRequestDTO productRequestDTO);

    public ProductResponseDTO updateProduct(String id, ProductUpdationRequestDTO productRequestDTO);

    public List<ProductResponseDTO> getProductsByFilter(Category category, Double minPrice, Double maxPrice);

    public void deleteProduct(String id);
}
