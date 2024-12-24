package com.example.store.service.impl;

import com.example.store.dto.ProductResponseDTO;
import com.example.store.model.Category;
import com.example.store.model.Product;
import com.example.store.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductFilterService {


    @Cacheable(value = "filteredProducts", key = "#minPrice + #maxPrice", unless = "#result.size() == 0")
    public List<ProductResponseDTO> getProductsByPriceFilter(Double minPrice, Double maxPrice, ProductRepository productRepository, ModelMapper modelMapper) {
        List<Product> filteredProducts = productRepository.findByPriceBetween(minPrice, maxPrice);

        return filteredProducts.stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "filteredProducts", key = "#category.toString() + #minPrice + #maxPrice", unless = "#result.size() == 0")
    public List<ProductResponseDTO> getProductsByAllFilters(Category category, Double minPrice, Double maxPrice, ProductRepository productRepository, ModelMapper modelMapper) {
        List<Product> filteredProducts = productRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice);

        return filteredProducts.stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }
}
