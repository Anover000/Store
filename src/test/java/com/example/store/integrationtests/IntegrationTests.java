package com.example.store.integrationtests;

import com.example.store.model.Category;
import com.example.store.model.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    private static String productId;
    private static String productId2;

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTests.class);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }


    @Test
    @Order(1)
    public void testAddProduct() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType("application/json")
                        .content(getRequestBody()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response);
        productId = node.get("id").asText();
        logger.info(productId);
        assertThat(productId).isNotNull();
    }

    @Test
    @Order(2)
    public void testGetProductById() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/products/" + productId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Laptop"));

        assertThat(cacheManager.getCache("products").get(productId)).isNotNull();

        //verify(productRepository, times(1)).findById("1"); //(to check the count of cache hit, but not possible here)
    }

    @Test
    @Order(3)
    public void testGetProducts() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].category").value("ELECTRONICS"));

        assertThat(cacheManager.getCacheNames()).contains("filteredProducts");
        assertThat(cacheManager.getCache("filteredProducts")).isNotNull();
        Cache cache = cacheManager.getCache("filteredProducts");
        Cache.ValueWrapper cachedValue = cache.get("1.7976931348623157E308");
        assertThat(cachedValue).isNotNull();
    }

    @Test
    @Order(4)
    public void updateProduct() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/products/" + productId)
                .contentType("application/json")
                .content(getUpdateRequestBody()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated Computing Device"));

        Cache cache = cacheManager.getCache("filteredProducts");
        Cache.ValueWrapper cachedValue = cache.get("1.7976931348623157E308");
        assertThat(cachedValue).isNull();

        assertThat(cacheManager.getCache("products").get(productId)).isNotNull();;

    }

    @Test
    @Order(5)
    public void testAddAnotherProduct() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType("application/json")
                        .content(getRequestBody()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response);
        productId2 = node.get("id").asText();
        logger.info(productId2);
        assertThat(productId).isNotNull();
    }

    @Test
    @Order(6)
    public void testGetAnotherProductById() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/products/" + productId2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Laptop"));

        assertThat(cacheManager.getCache("products").get(productId2)).isNotNull();
    }

    @Test
    @Order(7)
    public void deleteProduct() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/" + productId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertThat(cacheManager.getCache("products").get(productId)).isNull();
        assertThat(cacheManager.getCache("products").get(productId2)).isNotNull();

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/" + productId2))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertThat(cacheManager.getCache("products").get(productId2)).isNull();
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

    private String getRequestBody() {
        return "{\"name\": \"Laptop\"" +
                ", \"description\": \"Computing Device\"" +
                ", \"category\": \"ELECTRONICS\"" +
                ", \"price\": 1200.0" +
                ", \"quantity\": 10}";
    }

    private String getUpdateRequestBody() {
        return "{\"description\": \"Updated Computing Device\"}";
    }
}
