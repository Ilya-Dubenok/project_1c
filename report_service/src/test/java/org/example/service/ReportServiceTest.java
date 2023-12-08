package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.example.base.BaseRepositoryContainerTest;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.product.ItemDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.report.ProductToBuyDTO;
import org.example.core.dto.rule.ExpirationRuleDTO;
import org.example.core.dto.rule.QuantityRuleDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.core.dto.rule.RuleType;
import org.example.dao.entities.Report;
import org.example.dao.entities.ReportData;
import org.example.dao.repository.IReportRepository;
import org.example.service.api.IReportService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
@DisplayName("Testing report service")
public class ReportServiceTest extends BaseRepositoryContainerTest {

    @RegisterExtension
    public static WireMockExtension categoryServer = WireMockExtension.newInstance()
            .options(wireMockConfig().port(9595).notifier(new ConsoleNotifier(true)))
            .build();

    @RegisterExtension
    public static WireMockExtension productServer = WireMockExtension.newInstance()
            .options(wireMockConfig().port(9599).notifier(new ConsoleNotifier(true)))
            .build();

    @Autowired
    private IReportRepository repository;

    @Autowired
    private IReportService reportService;

    @Autowired
    private ObjectMapper jacksonObjectMapper;


    private final Map<String, CategoryDTO> categoriesMap = new HashMap<>();

    private final Map<String, ProductDTO> productsMap = new HashMap<>();

    @BeforeEach
    public void refreshInitialStructure() {
        formBasicCategoryTrees();
        formBasicProducts();
        refreshDefaultStubbing();
    }

    @AfterEach
    public void deleteAllData() {
        repository.deleteAll();
    }

    @Test
    public void productList_withAllRulesInProduct() {
        updateProductData("chicken", List.of(createItemWithQuantityAndExpiresAt(1, 2)), createListOfRulesWithForValues(2, 5));
        List<ProductToBuyDTO> productsToBuyDTOList = reportService.getProductsToBuyDTO();
        ProductToBuyDTO productToBuyDTO = productsToBuyDTOList.get(0);
        Assertions.assertEquals(2, productToBuyDTO.getQuantity());
        Assertions.assertEquals(1, productsToBuyDTOList.size());
    }

    @Test
    public void productList_withSingleRuleFromProduct_andAnotherFromCategory() {
        updateProductData("chicken", List.of(createItemWithQuantityAndExpiresAt(1, 2)), createListOfRulesWithForValues(2, null));
        updateCategoryData("meat", createListOfRulesWithForValues(null, 5));
        List<ProductToBuyDTO> productsToBuyDTOList = reportService.getProductsToBuyDTO();
        ProductToBuyDTO productToBuyDTO = productsToBuyDTOList.get(0);
        Assertions.assertEquals(2, productToBuyDTO.getQuantity());
        Assertions.assertEquals(1, productsToBuyDTOList.size());
    }

    @Test
    public void productList_withNoRuleFromProduct_oneFromCategory() {
        updateProductData("chicken", List.of(createItemWithQuantityAndExpiresAt(1, 2)));
        updateCategoryData("meat", createListOfRulesWithForValues(null, 5));
        List<ProductToBuyDTO> productsToBuyDTOList = reportService.getProductsToBuyDTO();
        ProductToBuyDTO productToBuyDTO = productsToBuyDTOList.get(0);
        Assertions.assertEquals(1, productToBuyDTO.getQuantity());
        Assertions.assertEquals(1, productsToBuyDTOList.size());
    }

    @Test
    public void productList_withNoRuleFromProduct_twoFromCategory() {
        updateProductData("chicken", List.of(createItemWithQuantityAndExpiresAt(1, 2)));
        updateCategoryData("meat", createListOfRulesWithForValues(2, 5));
        List<ProductToBuyDTO> productsToBuyDTOList = reportService.getProductsToBuyDTO();
        ProductToBuyDTO productToBuyDTO = productsToBuyDTOList.get(0);
        Assertions.assertEquals(2, productToBuyDTO.getQuantity());
        Assertions.assertEquals(1, productsToBuyDTOList.size());
    }

    @Test
    public void formReportData() {
        updateProductData("chicken", List.of(createItemWithQuantityAndExpiresAt(1, 2)), createListOfRulesWithForValues(2, 5));
        reportService.formReport();
        List<ReportData> data = repository.findAll().get(0).getData();
        Assertions.assertEquals(1, data.size());
        Assertions.assertEquals("food products", data.get(0).getCategory().getName());
    }

    private void updateProductServerStubbing() {
        try {
            productServer.stubFor(get(urlEqualTo("/internal/all_products"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(jacksonObjectMapper.writeValueAsString(productsMap.values()))
                            .withHeader("Content-Type", "application/json")));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ItemDTO createItemWithQuantityAndExpiresAt(int quantity, int daysSinceCurrentDay) {
        return new ItemDTO(quantity, LocalDate.now().plus(daysSinceCurrentDay, ChronoUnit.DAYS));
    }

    private List<RuleDTO> createListOfRulesWithForValues(Integer quantity, Integer daysTillExpiration) {
        List<RuleDTO> rules = new ArrayList<>();
        if (quantity != null && quantity > 0) {
            rules.add(new QuantityRuleDTO(RuleType.QUANT, quantity));
        }
        if (daysTillExpiration != null && daysTillExpiration > 0) {
            rules.add(new ExpirationRuleDTO(RuleType.EXP, daysTillExpiration));
        }
        return rules;
    }


    private void updateCategoryData(String categoryName, List<RuleDTO> rules) {
        CategoryDTO categoryDTO = categoriesMap.get(categoryName);
        categoryDTO.setRules(rules);
        try {
            updateCategoryServerStubbing(categoryName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateProductData(String productName, List<ItemDTO> items) {
        ProductDTO productDTO = productsMap.get(productName);
        productDTO.setItems(items);
        updateProductServerStubbing();
    }

    private void updateProductData(String productName, List<ItemDTO> items, List<RuleDTO> rules) {
        ProductDTO productDTO = productsMap.get(productName);
        productDTO.setItems(items);
        productDTO.setRules(rules);
        updateProductServerStubbing();
    }

    private void refreshDefaultStubbing() {
        updateProductServerStubbing();
        updateGeneralCategoryServerStubbing();
    }

    private void updateGeneralCategoryServerStubbing() {
        try {
            categoryServer.stubFor(get(urlMatching("/internal/exists/(.+)"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(jacksonObjectMapper.writeValueAsString(true))
                            .withHeader("Content-Type", "application/json")));

            categoryServer.stubFor(get(urlMatching("/internal/applicable_rules/(.+)")).atPriority(500)
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(jacksonObjectMapper.writeValueAsString(new ArrayList<>()))
                            .withHeader("Content-Type", "application/json")));

            categoriesMap.values().forEach(category -> {
                try {
                    categoryServer.stubFor(get(urlMatching("/internal/category_and_parents/" + category.getId()))
                            .willReturn(aResponse()
                                    .withStatus(200)
                                    .withBody(jacksonObjectMapper.writeValueAsString(getCategoryAndParents(category)))
                                    .withHeader("Content-Type", "application/json")));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<CategoryDTO> getCategoryAndParents(CategoryDTO categoryDTO) {
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        categoryDTOList.add(categoryDTO);
        while (categoryDTO.getParentId() != null) {
            UUID parentId = categoryDTO.getParentId();
            CategoryDTO parent = categoriesMap.get(categoriesMap.values().stream().filter(x -> x.getId().equals(parentId)).findAny().orElseThrow().getName());
            categoryDTOList.add(parent);
            categoryDTO = parent;
        }
        return categoryDTOList;
    }

    private void updateCategoryServerStubbing(String categoryName) throws JsonProcessingException {
        CategoryDTO category = categoriesMap.get(categoryName);
        categoryServer.stubFor(get(urlPathEqualTo("/internal/applicable_rules/" + category.getId())).atPriority(5)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jacksonObjectMapper.writeValueAsString(category.getRules()))
                        .withHeader("Content-Type", "application/json")));
    }

    private void formBasicCategoryTrees() {
        addBasicCategory("medkit");
        addBasicCategory("painkillers", "medkit");
        addBasicCategory("flu", "medkit");

        addBasicCategory("food products");
        addBasicCategory("food", "food products");
        addBasicCategory("drinks", "food products");

        addBasicCategory("meat", "food");
        addBasicCategory("coffee", "drinks");
        addBasicCategory("tea", "drinks");
    }

    private void formBasicProducts() {
        addBasicProduct("tempalgin", "painkillers");
        addBasicProduct("antiflu", "flu");

        addBasicProduct("chicken", "meat");
        addBasicProduct("lavazza", "coffee");
        addBasicProduct("jacobs", "coffee");
        addBasicProduct("earl grey", "tea");
    }

    private void addBasicCategory(String categoryName) {
        CategoryDTO categoryDTO = new CategoryDTO(UUID.randomUUID(), categoryName, null, new ArrayList<>());
        categoriesMap.put(categoryName, categoryDTO);
    }

    private void addBasicCategory(String categoryName, String parentCategoryName) {
        CategoryDTO categoryDTO = new CategoryDTO(UUID.randomUUID(), categoryName, categoriesMap.get(parentCategoryName).getId(), new ArrayList<>());
        categoriesMap.put(categoryDTO.getName(), categoryDTO);
    }

    private void addBasicProduct(String productName, String categoryName) {
        ProductDTO productDTO = new ProductDTO(UUID.randomUUID(), categoriesMap.get(categoryName).getId(), productName, null, null);
        productsMap.put(productName, productDTO);
    }

}
