package org.example.service.api.clients;

import org.example.core.dto.product.ProductDTO;
import org.example.service.api.clients.fallback.ProductClientFallBackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "product-service", path = "/internal", fallbackFactory = ProductClientFallBackFactory.class)
public interface IProductClient {

    @RequestMapping(path = "/all_products")
    List<ProductDTO> getProductsList();
}
