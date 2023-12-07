package org.example.service;

import org.example.base.BaseRepositoryContainerTest;
import org.example.core.dto.product.ItemDTO;
import org.example.core.dto.product.ProductCreateDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.rule.ExpirationRuleDTO;
import org.example.core.dto.rule.QuantityRuleDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.core.exception.InternalException;
import org.example.dao.entities.product.RuleType;
import org.example.dao.repository.IProductRepository;
import org.example.service.api.IProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SpringBootTest
@DisplayName("Testing product service")
@AutoConfigureWireMock(port = 9595)
public class ProductServiceTest extends BaseRepositoryContainerTest {

    private static final String NON_EXISTING_CATEGORY_ID = "af849fc5-74af-43fd-9a3c-adb8270522e8";

    @Autowired
    private IProductRepository repository;

    @Autowired
    private IProductService productService;

    @AfterEach
    public void deleteAllData() {
        repository.deleteAll();
    }

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

    @Test
    public void saveWithManySameRules() {
        List<RuleDTO> multipleRules = List.of(new ExpirationRuleDTO(RuleType.EXP, 2), new ExpirationRuleDTO(RuleType.EXP, 5), new QuantityRuleDTO(RuleType.QUANT, 6), new QuantityRuleDTO(RuleType.QUANT, 15));
        ProductDTO savedProduct = productService.save(new ProductCreateDTO("name", UUID.randomUUID(), multipleRules, null));
        Set<RuleType> allCurrentRuleTypes = new HashSet<>(List.of(RuleType.values()));
        long totalRulesWithDifferentRuleTypes = savedProduct.getRules().stream()
                .filter(o -> allCurrentRuleTypes.remove(o.getRuleType()))
                .count();
        Assertions.assertEquals(2, totalRulesWithDifferentRuleTypes);
    }

    @Test
    public void saveWithSameExpiresAtItems() {
        List<ItemDTO> items = List.of(new ItemDTO(3, LocalDate.now()), new ItemDTO(2, LocalDate.now()), new ItemDTO(5, LocalDate.now()), new ItemDTO(7, LocalDate.now().plus(1, ChronoUnit.DAYS)));
        ProductDTO savedProduct = productService.save(new ProductCreateDTO("name", UUID.randomUUID(), null, items));
        List<ItemDTO> savedItems = savedProduct.getItems();
        Assertions.assertEquals(2, savedItems.size());
        long matchedSavedItems = savedItems.stream()
                .filter(item -> {
                    if (item.getExpiresAt().isEqual(LocalDate.now())) {
                        return item.getQuantity().equals(10);
                    } else {
                        return item.getQuantity().equals(7);
                    }
                })
                .count();
        Assertions.assertEquals(2, matchedSavedItems);
    }

    @Test
    public void findByNameWorks() {
        productService.save(new ProductCreateDTO("name", UUID.randomUUID(), null, null));
        Assertions.assertEquals("name", productService.findByName("name").getName());
    }


}
