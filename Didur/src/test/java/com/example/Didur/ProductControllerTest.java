package com.example.Didur;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- GET ALL ---
    @Test
    void getAllProducts_Success() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // --- GET BY ID ---
    @Test
    void getProductById_Success() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getProductById_NotFound() throws Exception {
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());
    }

    // --- POST ---
    @Test
    void createProduct_Success() throws Exception {
        String jsonBody = "{\"name\":\"Tablet\",\"price\":500.0,\"dueDate\":\"2026-12-31\"}";

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tablet"))
                .andExpect(jsonPath("$.price").value(500.0));
    }

    // --- PUT ---
    @Test
    void updateProduct_Success() throws Exception {
        String jsonBody = "{\"name\":\"Laptop Pro\",\"price\":1500.0,\"dueDate\":\"2026-12-31\"}";

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop Pro"));
    }

    @Test
    void updateProduct_NotFound() throws Exception {
        String jsonBody = "{\"name\":\"NonExistent\",\"price\":100.0,\"dueDate\":\"2026-12-31\"}";

        mockMvc.perform(put("/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isNotFound());
    }

    // --- DELETE ---
    @Test
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(delete("/products/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/products/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_NotFound() throws Exception {
        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isNotFound());
    }
}