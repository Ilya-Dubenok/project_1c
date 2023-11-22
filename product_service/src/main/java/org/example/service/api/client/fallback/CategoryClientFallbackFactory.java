package org.example.service.api.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.example.core.exception.OtherServiceUnavailableException;
import org.example.service.api.client.ICategoryClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CategoryClientFallbackFactory implements FallbackFactory<ICategoryClient> {

    @Override
    public ICategoryClient create(Throwable cause) {
        log.error("[FALLBACK] Could not reach category-service", cause);
        return new ICategoryClient() {
            @Override
            public Boolean categoryExists(UUID categoryUuid) {
                throw new OtherServiceUnavailableException();
            }
        };
    }
}
