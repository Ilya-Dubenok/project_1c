package org.example.service.api;

import lombok.extern.slf4j.Slf4j;
import org.example.core.exception.OtherServiceUnavailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient(name = "category-service", path = "/internal", fallbackFactory = ICategoryClient.CategoryClientFallbackFactory.class)
public interface ICategoryClient {

    @RequestMapping(method = RequestMethod.GET, path = "/exists/{uuid}")
    Boolean categoryExists(@PathVariable(name = "uuid") UUID categoryUuid);

    @Slf4j
    @Component
    class CategoryClientFallbackFactory implements FallbackFactory<ICategoryClient> {

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

}
