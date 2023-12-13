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
        Assertions.assertDoesNotThrow(() -> saveDefaultProduct());
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
        saveDefaultProduct("name");
        Assertions.assertEquals("name", productService.findByName("name").getName());
    }


    @Test
    public void updateNameSimple() {
        String oldName = "old name";
        UUID savedProductId = saveDefaultProduct(oldName).getId();
        String newName = "new name";
        productService.updateName(savedProductId, newName);
        Assertions.assertEquals(newName, productService.findById(savedProductId).getName());
    }

    @Test
    public void updateNameWithConflict() {
        String existingName = "existing name";
        saveDefaultProduct(existingName);
        String newName = "new name";
        ProductDTO anotherSavedProduct = saveDefaultProduct(newName);
        Assertions.assertThrows(DuplicateKeyException.class, () -> productService.updateName(anotherSavedProduct.getId(), existingName));
    }

    @Test
    public void addItemSimple() {
        UUID savedProductId = saveDefaultProduct().getId();
        productService.addItem(savedProductId, new ItemDTO(3, LocalDate.now()));
        productService.addItem(savedProductId, new ItemDTO(4, LocalDate.now().plus(1, ChronoUnit.DAYS)));
        Assertions.assertEquals(2, productService.findById(savedProductId).getItems().size());
    }

    @Test
    public void addItem_withOverlappingExpirationDate() {
        UUID savedProductId = saveDefaultProduct().getId();
        productService.addItem(savedProductId, new ItemDTO(5, LocalDate.now()));
        productService.addItem(savedProductId, new ItemDTO(5, LocalDate.now()));
        productService.addItem(savedProductId, new ItemDTO(7, LocalDate.now().plus(1, ChronoUnit.DAYS)));
        List<ItemDTO> savedItems = productService.findById(savedProductId).getItems();
        long matchedSavedItems = countItemsForDateQuantityAndRemainder(savedItems, LocalDate.now(), 10, 7);
        Assertions.assertEquals(2, matchedSavedItems);
    }

    @Test
    public void addToItemQuantity_whenNoItemsExist_Throws() {
        UUID productId = saveDefaultProduct().getId();
        Assertions.assertThrows(InternalException.class, () -> productService.addToItemQuantity(productId, LocalDate.now().plus(1, ChronoUnit.DAYS), 3));
    }

    @Test
    public void addToItemQuantity_whenNoItemsFound_Throws() {
        UUID productId = saveDefaultProduct().getId();
        productService.addItem(productId, new ItemDTO(2, LocalDate.now()));
        Assertions.assertThrows(InternalException.class, () -> productService.addToItemQuantity(productId, LocalDate.now().plus(1, ChronoUnit.DAYS), 3));
    }

    @Test
    public void addToItemQuantity_withResultLessThenZero() {
        UUID productId = saveDefaultProduct().getId();
        LocalDate expiresAt = LocalDate.now();
        productService.addItem(productId, new ItemDTO(2, expiresAt));
        productService.addToItemQuantity(productId, expiresAt, -4);
        Assertions.assertEquals(0, productService.findById(productId).getItems().size());
    }

    @Test
    public void addToItemQuantity_withResultEqualToZero() {
        UUID productId = saveDefaultProduct().getId();
        LocalDate expiresAt = LocalDate.now();
        productService.addItem(productId, new ItemDTO(2, expiresAt));
        productService.addToItemQuantity(productId, expiresAt, -2);
        Assertions.assertEquals(0, productService.findById(productId).getItems().size());
    }

    @Test
    public void addToItemQuantity_withResultGreaterThanZero() {
        UUID productId = saveDefaultProduct().getId();
        LocalDate expiresAt = LocalDate.now();
        productService.addItem(productId, new ItemDTO(5, expiresAt));
        productService.addToItemQuantity(productId, expiresAt, -2);
        ItemDTO itemDTO = getListOfStoredItemsForProduct(productId).get(0);
        Assertions.assertEquals(3, itemDTO.getQuantity());
    }

    @Test
    public void changeItemExpirationDateSimple() {
        UUID productId = saveDefaultProduct().getId();
        LocalDate expiresAt = LocalDate.now();
        productService.addItem(productId, new ItemDTO(5, expiresAt));
        LocalDate replacement = expiresAt.plus(1, ChronoUnit.DAYS);
        productService.changeItemExpirationDate(productId, expiresAt, replacement);
        ItemDTO itemDTO = getListOfStoredItemsForProduct(productId).get(0);
        Assertions.assertEquals(replacement, itemDTO.getExpiresAt());
    }

    @Test
    public void changeItemExpirationDate_withResultOverlap() {
        UUID productId = saveDefaultProduct().getId();
        LocalDate expiresAt = LocalDate.now();
        productService.addItem(productId, new ItemDTO(3, expiresAt));
        LocalDate secondExpiresAt = expiresAt.plus(1, ChronoUnit.DAYS);
        productService.addItem(productId, new ItemDTO(7, secondExpiresAt));
        productService.changeItemExpirationDate(productId, expiresAt, secondExpiresAt);
        List<ItemDTO> itemsStored = getListOfStoredItemsForProduct(productId);
        Assertions.assertEquals(1, itemsStored.size());
        ItemDTO mergedItem = itemsStored.get(0);
        Assertions.assertEquals(10, mergedItem.getQuantity());
        Assertions.assertEquals(secondExpiresAt, mergedItem.getExpiresAt());
    }

    private ProductDTO saveDefaultProduct() {
        return productService.save(new ProductCreateDTO("name", UUID.randomUUID(), null, null));
    }

    private ProductDTO saveDefaultProduct(String name) {
        return productService.save(new ProductCreateDTO(name, UUID.randomUUID(), null, null));
    }

    private List<ItemDTO> getListOfStoredItemsForProduct(UUID productId) {
        return productService.findById(productId).getItems();
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
