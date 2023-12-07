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
import org.springframework.dao.DuplicateKeyException;

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
        long matchedSavedItems = countItemsForDateQuantityAndRemainder(savedItems, LocalDate.now(), 10, 7);
        Assertions.assertEquals(2, matchedSavedItems);
    }

    @Test
    public void findByNameWorks() {
        productService.save(new ProductCreateDTO("name", UUID.randomUUID(), null, null));
        Assertions.assertEquals("name", productService.findByName("name").getName());
    }


    @Test
    public void updateNameSimple() {
        String oldName = "old name";
        UUID savedProductId = productService.save(new ProductCreateDTO(oldName, UUID.randomUUID(), null, null)).getId();
        String newName = "new name";
        productService.updateName(savedProductId, newName);
        Assertions.assertEquals(newName, productService.findById(savedProductId).getName());
    }

    @Test
    public void updateNameWithConflict() {
        String existingName = "existing name";
        productService.save(new ProductCreateDTO(existingName, UUID.randomUUID(), null, null));
        String newName = "new name";
        ProductDTO anotherSavedProduct = productService.save(new ProductCreateDTO(newName, UUID.randomUUID(), null, null));
        Assertions.assertThrows(DuplicateKeyException.class, () -> productService.updateName(anotherSavedProduct.getId(), existingName));
    }

    @Test
    public void addItemSimple() {
        UUID savedProductId = productService.save(new ProductCreateDTO("name", UUID.randomUUID(), null, null)).getId();
        productService.addItem(savedProductId, new ItemDTO(3, LocalDate.now()));
        productService.addItem(savedProductId, new ItemDTO(4, LocalDate.now().plus(1, ChronoUnit.DAYS)));
        Assertions.assertEquals(2, productService.findById(savedProductId).getItems().size());
    }

    @Test
    public void addItemWithOverlappingExpirationDate() {
        UUID savedProductId = productService.save(new ProductCreateDTO("name", UUID.randomUUID(), null, null)).getId();
        productService.addItem(savedProductId, new ItemDTO(5, LocalDate.now()));
        productService.addItem(savedProductId, new ItemDTO(5, LocalDate.now()));
        productService.addItem(savedProductId, new ItemDTO(7, LocalDate.now().plus(1, ChronoUnit.DAYS)));
        List<ItemDTO> savedItems = productService.findById(savedProductId).getItems();
        long matchedSavedItems = countItemsForDateQuantityAndRemainder(savedItems, LocalDate.now(), 10, 7);
        Assertions.assertEquals(2, matchedSavedItems);
    }

    private static long countItemsForDateQuantityAndRemainder(List<ItemDTO> savedItems, LocalDate firstDate, int quantityForFirstDate, int remainder) {
        return savedItems.stream().filter(item -> {
            if (item.getExpiresAt().isEqual(firstDate)) {
                return item.getQuantity().equals(quantityForFirstDate);
            } else {
                return item.getQuantity().equals(remainder);
            }
        }).count();
    }


}
