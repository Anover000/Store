package com.example.store.unittests;

import com.example.store.dto.ProductCreationRequestDTO;
import com.example.store.dto.ProductResponseDTO;
import com.example.store.dto.ProductUpdationRequestDTO;
import com.example.store.model.Category;
import com.example.store.model.Product;
import com.example.store.repository.ProductRepository;
import com.example.store.service.impl.ProductFilterService;
import com.example.store.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceUnitTests {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CacheManager cacheManager;

    private Product product;
    private ProductCreationRequestDTO productRequestDTO;
    private ProductUpdationRequestDTO productUpdationRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ModelMapper modelMapper = new ModelMapper();
        ProductFilterService productFilterService = new ProductFilterService();
        productService = new ProductServiceImpl(productRepository, modelMapper, productFilterService);
        product = getProduct();
        productRequestDTO = getProductCreationRequestDTO();
        productUpdationRequestDTO = getProductUpdationRequestDTO();
    }

    @Test
    public void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));
        List<ProductResponseDTO> result = productService.getAllProducts();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    public void testGetProductById() {
        when(productRepository.findById("1")).thenReturn(Optional.ofNullable(product));
        ProductResponseDTO result = productService.getProductById("1");
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");
    }

    @Test
    public void testAddProduct() {
        Product productWithoutID = getProduct();
        productWithoutID.setId(null);
        when(productRepository.save(productWithoutID)).thenReturn(product);
        ProductResponseDTO result = productService.addProduct(productRequestDTO);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");
    }

    @Test
    public void testUpdateProduct() {
        Product updatedProduct = getProduct();
        updatedProduct.setDescription("Updated Computing Device");
        updatedProduct.setQuantity(20);
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);

        ProductResponseDTO result = productService.updateProduct("1", productUpdationRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Updated Computing Device");
    }

    @Test
    public void testDeleteProduct() {
        doNothing().when(productRepository).deleteById("1");

        productService.deleteProduct("1");

        verify(productRepository, times(1)).deleteById("1");
    }

    @Test
    public void testGetProductsByFilter() {

        when(productRepository.findByPriceBetween(500, 1500)).thenReturn(Collections.singletonList(product));
        List<ProductResponseDTO> result = productService.getProductsByFilter(null, 500.0, 1500.0);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
    }


    private Product getProduct() {
        Product product = new Product();
        product.setId("1");
        product.setName("Laptop");
        product.setPrice(1200.0);
        product.setDescription("Computing Device");
        product.setQuantity(10);
        product.setCategory(Category.ELECTRONICS);

        return product;
    }

    private ProductResponseDTO getProductResponseDTO() {
        ProductResponseDTO product = new ProductResponseDTO();
        product.setId("1");
        product.setName("Laptop");
        product.setPrice(1200.0);
        product.setDescription("Computing Device");
        product.setCategory(Category.ELECTRONICS);

        return product;
    }

    private ProductUpdationRequestDTO getProductUpdationRequestDTO() {
        ProductUpdationRequestDTO product = new ProductUpdationRequestDTO();
        product.setDescription("Updated Computing Device");
        product.setQuantity(20);
        return product;
    }

    private ProductCreationRequestDTO getProductCreationRequestDTO() {
        ProductCreationRequestDTO product = new ProductCreationRequestDTO();;
        product.setName("Laptop");
        product.setPrice(1200.0);
        product.setDescription("Computing Device");
        product.setCategory(Category.ELECTRONICS);
        product.setQuantity(10);
        return product;
    }
}
