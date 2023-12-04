package org.example.enpoint.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.JwtSecurityConfig;
import org.example.core.dto.product.ProductCreateDTO;
import org.example.dao.repository.IProductRepository;
import org.example.endpoint.web.ProductController;
import org.example.service.api.IProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {JwtSecurityConfig.class, ProductController.class})
@AutoConfigureMockMvc
public class SecurityProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private IProductService reportService;

    @MockBean
    private IProductRepository reportRepository;

    @Test
    public void unauthenticatedAccessFails() throws Exception {
        ProductCreateDTO productCreateDTO = new ProductCreateDTO("Some name", UUID.randomUUID(), null, null);
        mockMvc.perform(post("/product")
                        .header("gateway", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productCreateDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void authenticatedAccessSucceeds() throws Exception {
        ProductCreateDTO productCreateDTO = new ProductCreateDTO("Some name", UUID.randomUUID(), null, null);
        mockMvc.perform(post("/product")
                        .header("gateway", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productCreateDTO)))
                .andExpect(status().isOk());
    }



}
