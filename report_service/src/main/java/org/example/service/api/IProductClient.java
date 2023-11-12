package org.example.service.api;

import org.example.core.dto.product.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

//TODO ADD FALLBACK METHOD
@FeignClient(name = "product-service", path = "/internal")
public interface IProductClient {

    @RequestMapping(path = "/all_products")
    List<ProductDTO> getProductsList();
}
