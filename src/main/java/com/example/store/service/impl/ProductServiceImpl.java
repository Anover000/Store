package com.example.store.service.impl;

import com.example.store.dto.ProductCreationRequestDTO;
import com.example.store.dto.ProductUpdationRequestDTO;
import com.example.store.dto.ProductResponseDTO;
import com.example.store.exception.ProductNotFoundException;
import com.example.store.model.Category;
import com.example.store.model.Product;
import com.example.store.repository.ProductRepository;
import com.example.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Autowired
    ProductFilterService productFilterService;

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));

        return modelMapper.map(product, ProductResponseDTO.class);
    }

    @Override
    @CacheEvict(value = {"filteredProducts"}, allEntries = true)
    public ProductResponseDTO addProduct(ProductCreationRequestDTO productRequestDTO) {

        Product product = modelMapper.map(productRequestDTO, Product.class);
        product =  productRepository.save(product);
        return modelMapper.map(product, ProductResponseDTO.class);
    }

    @Override
    @CachePut(value = "products", key = "#id")
    @CacheEvict(value = "filteredProducts", allEntries = true)
    public ProductResponseDTO updateProduct(String id, ProductUpdationRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));

        if(productRequestDTO.getDescription() != null && !productRequestDTO.getDescription().trim().isEmpty()) {
            product.setDescription(productRequestDTO.getDescription());
        }

        if(productRequestDTO.getPrice() != null ) {
            product.setPrice(productRequestDTO.getPrice());
        }

        if(productRequestDTO.getQuantity() != null) {
            product.setQuantity(productRequestDTO.getQuantity());
        }

        return modelMapper.map(productRepository.save(product), ProductResponseDTO.class);
    }

    @Override
    public List<ProductResponseDTO> getProductsByFilter(Category category, Double minPrice, Double maxPrice) {

        if(minPrice == null) {
            minPrice = 0.0;
        }

        if(maxPrice == null) {
            maxPrice = Double.MAX_VALUE;
        }

        List<Product> filteredProducts;

        if(category == null) {
            return productFilterService.getProductsByPriceFilter(minPrice, maxPrice, productRepository, modelMapper);
        }
        else {
            return productFilterService.getProductsByAllFilters(category, minPrice, maxPrice, productRepository, modelMapper);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "filteredProducts", allEntries = true),
            @CacheEvict(value = "products", key = "#id")
    })
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
