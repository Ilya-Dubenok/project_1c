package org.example.endpoint.web;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.example.core.dto.product.ProductDTO;
import org.example.service.api.IProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Hidden
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/internal")
public class InternalRequestsController {

    private final IProductService productService;

    @GetMapping(value = "/all_products")
    public List<ProductDTO> getAll() {
        return productService.findAll();
    }

}
