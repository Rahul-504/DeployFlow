
package com.rahul.smartapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.rahul.smartapi.entity.Product;
import com.rahul.smartapi.repository.ProductRepository;
import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void getMissingProductShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/products/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Product not found with id: 999999"));
    }

    @Test
    void createProductShouldReturn201() throws Exception {
        String productJson = """
                {
                    "name": "Test Product",
                    "description": "Automated test product",
                    "price": 100,
                    "stock": 10
                }
                """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void invalidProductShouldReturn400() throws Exception {
        String invalidProductJson = """
                {
                    "name": "",
                    "description": "Invalid product",
                    "price": 100,
                    "stock": 10
                }
                """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProductJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateProductShouldReturn200() throws Exception {
        Product product = new Product();
        product.setName("Old Product");
        product.setDescription("Before update");
        product.setPrice(new BigDecimal("50.00"));
        product.setStock(5);

        Product savedProduct = productRepository.save(product);

        String updatedJson = """
                {
                    "name": "Updated Product",
                    "description": "Updated description",
                    "price": 150,
                    "stock": 20
                }
                """;

        mockMvc.perform(put("/api/products/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.stock").value(20));
    }

    @Test
    void deleteProductShouldReturn204() throws Exception {
        Product product = new Product();
        product.setName("Product To Delete");
        product.setDescription("Delete test");
        product.setPrice(new BigDecimal("25.00"));
        product.setStock(3);

        Product savedProduct = productRepository.save(product);

        mockMvc.perform(delete("/api/products/" + savedProduct.getId()))
                .andExpect(status().isNoContent());
    }

}
