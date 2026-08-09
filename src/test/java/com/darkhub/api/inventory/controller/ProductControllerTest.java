package com.darkhub.api.inventory.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String loginAsAdmin() throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"admin123"}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return body.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    private String loginAsUser() throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"demo","password":"demo1234"}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return body.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void adminCanCreateProduct_returns201() throws Exception {
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + loginAsAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test Product","description":"from test","price":12.50}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(12.50));
    }

    @Test
    void userCannotCreateProduct_returns403() throws Exception {
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + loginAsUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Forbidden","price":1.0}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void createWithoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"No Token","price":1.0}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createMissingPrice_returns400() throws Exception {
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + loginAsAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"No Price"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("price")));
    }

    @Test
    void userCanListProducts_returns200() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + loginAsUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(5)));
    }

    @Test
    void getMissingProduct_returns404() throws Exception {
        mockMvc.perform(get("/api/products/999999")
                        .header("Authorization", "Bearer " + loginAsUser()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void pagination_returnsPageStructure() throws Exception {
        mockMvc.perform(get("/api/products/paged?page=0&size=2&search=usb")
                        .header("Authorization", "Bearer " + loginAsUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void adminCanUpdateAndDeleteProduct() throws Exception {
        String token = loginAsAdmin();

        String createBody = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"To Update","price":5.0}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = Long.parseLong(createBody.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(put("/api/products/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Updated","description":"changed","price":9.99}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));

        mockMvc.perform(delete("/api/products/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}