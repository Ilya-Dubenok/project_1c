package org.example.service;

import org.example.base.BaseRepositoryContainerTest;
import org.example.core.dto.product.ProductCreateDTO;
import org.example.core.exception.InternalException;
import org.example.dao.repository.IProductRepository;
import org.example.service.api.IProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.util.Collections;
import java.util.UUID;

@SpringBootTest
@DisplayName("Testing product service")
@AutoConfigureWireMock(port = 9595)
public class ProductServiceTest extends BaseRepositoryContainerTest {

    private static final String NON_EXISTING_CATEGORY_ID = "af849fc5-74af-43fd-9a3c-adb8270522e8";

    @Autowired
    private IProductRepository repository;

    @Autowired
    private IProductService productService;

    @Test
    public void savingWithExistingCategoryIdSucceeds() {
        UUID categoryId = UUID.randomUUID();
        Assertions.assertDoesNotThrow(() -> productService.save(new ProductCreateDTO("some name", categoryId, Collections.emptyList(), Collections.emptyList())));
    }

    @Test
    public void savingWithNotExistingCategoryIdFails() {
        UUID categoryId = UUID.fromString(NON_EXISTING_CATEGORY_ID);
        Assertions.assertThrows(InternalException.class, () -> productService.save(new ProductCreateDTO("some name", categoryId, Collections.emptyList(), Collections.emptyList())));
    }

}
