package org.example.service.api.clients.fallback;

import lombok.extern.slf4j.Slf4j;
import org.example.core.dto.exception.OtherServiceUnavailableException;
import org.example.core.dto.product.ProductDTO;
import org.example.service.api.clients.IProductClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ProductClientFallBackFactory implements FallbackFactory<IProductClient> {

    @Override
    public IProductClient create(Throwable cause) {
        log.error("[FALLBACK] Could not reach category-service", cause);
        return new IProductClient() {

            @Override
            public List<ProductDTO> getProductsList() {
                throw new OtherServiceUnavailableException();
            }
        };
    }
}
