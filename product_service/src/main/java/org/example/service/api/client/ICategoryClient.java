package org.example.service.api.client;

import org.example.service.api.client.fallback.CategoryClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient(name = "category-service", path = "/internal", fallbackFactory = CategoryClientFallbackFactory.class)
public interface ICategoryClient {

    @RequestMapping(method = RequestMethod.GET, path = "/exists/{uuid}")
    Boolean categoryExists(@PathVariable(name = "uuid") UUID categoryUuid);

}
