package org.example.service;

import org.example.base.BaseRepositoryContainerTest;
import org.example.core.dto.category.CategoryCreateDTO;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.rule.ExpirationRuleCreateDTO;
import org.example.core.dto.rule.QuantityRuleCreateDTO;
import org.example.core.dto.rule.RuleCreateDTO;
import org.example.core.exception.EntityNotFoundException;
import org.example.dao.entities.RuleType;
import org.example.dao.repositories.ICategoryRepository;
import org.example.service.api.ICategoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootTest
@DisplayName("Test CategoryService Methods")
public class CategoryServiceTest extends BaseRepositoryContainerTest {

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ICategoryService categoryService;

    @AfterEach
    public void deleteAllData() {
        categoryRepository.deleteAll();
    }

    @Test
    public void saveWorks() {
        categoryService.save(new CategoryCreateDTO("test_name1", null, null));
    }

    @Test
    public void findByIdWorks() {
        System.out.println(BaseRepositoryContainerTest.postgres.isRunning());
        CategoryDTO testCategory = categoryService.save(new CategoryCreateDTO("test_name1", null, null));
        CategoryDTO extractedByFindMethodCategory = categoryService.findById(testCategory.getId());
        Assertions.assertEquals(testCategory, extractedByFindMethodCategory);
    }

    @Test
    public void findByWrongIdThrows() {
        categoryService.save(new CategoryCreateDTO("test_name1", null, null));
        Assertions.assertThrows(EntityNotFoundException.class, () -> categoryService.findById(UUID.randomUUID()));
    }

    @Test
    public void findCategoryAndParents() {
        CategoryDTO parent = categoryService.save(new CategoryCreateDTO("parent", null, null));
        CategoryDTO parentAndChild = categoryService.save(new CategoryCreateDTO("parentAndChild", parent.getId(), null));
        CategoryDTO child = categoryService.save(new CategoryCreateDTO("child", parentAndChild.getId(), null));
        CategoryDTO anotherCategory = categoryService.save(new CategoryCreateDTO("anotherCategory", null, null));

        List<CategoryDTO> childAndParents = categoryService.findCategoryAndParents(child.getId());
        long matchedToAnotherCategory = childAndParents.stream().filter(category -> Objects.equals("anotherCategory", category.getName())).count();
        Assertions.assertEquals(3, childAndParents.size());
        Assertions.assertEquals(0, matchedToAnotherCategory);
    }

    @Test
    public void findApplicableRules1() {
        CategoryDTO parentWithExpRule = categoryService.save(new CategoryCreateDTO("parentWithExpRule", null, List.of(new ExpirationRuleCreateDTO(15))));
        CategoryDTO parentAndChildWithQuantRule = categoryService.save(new CategoryCreateDTO("parentAndChildWithQuantRule", parentWithExpRule.getId(), List.of(new QuantityRuleCreateDTO(3))));
        CategoryDTO childWithNoRule = categoryService.save(new CategoryCreateDTO("childWithNoRule", parentAndChildWithQuantRule.getId(), null));
        Set<RuleType> ruleTypesToLookFor = Stream.of(RuleType.QUANT, RuleType.EXP).collect(Collectors.toSet());
        List<RuleCreateDTO> applicableRules = categoryService.findApplicableRules(childWithNoRule.getId(), ruleTypesToLookFor);
        Assertions.assertEquals(2, applicableRules.size());
    }

    @Test
    public void findApplicableRules2() {
        CategoryDTO parentWithExpRule = categoryService.save(new CategoryCreateDTO("parentWithExpRule", null, List.of(new ExpirationRuleCreateDTO(15))));
        CategoryDTO parentAndChildWithNoRule = categoryService.save(new CategoryCreateDTO("parentAndChildWithNoRule", parentWithExpRule.getId(), null));
        CategoryDTO childWithQuantRule = categoryService.save(new CategoryCreateDTO("childWithQuantRule", parentAndChildWithNoRule.getId(), List.of(new QuantityRuleCreateDTO(3))));
        Set<RuleType> ruleTypesToLookFor = Stream.of(RuleType.QUANT, RuleType.EXP).collect(Collectors.toSet());
        List<RuleCreateDTO> applicableRules = categoryService.findApplicableRules(childWithQuantRule.getId(), ruleTypesToLookFor);
        Assertions.assertEquals(2, applicableRules.size());
    }

    @Test
    public void findApplicableRules3() {
        QuantityRuleCreateDTO quantRuleToBeFound = new QuantityRuleCreateDTO(3);
        ExpirationRuleCreateDTO expRuleToBeFound = new ExpirationRuleCreateDTO(2);

        CategoryDTO parentWithAllRules = categoryService.save(new CategoryCreateDTO("parentWithAllRules", null, List.of(new ExpirationRuleCreateDTO(1), new QuantityRuleCreateDTO(1))));
        CategoryDTO parentAndChildWithExpirationRule = categoryService.save(new CategoryCreateDTO("parentAndChildWithExpirationRule", parentWithAllRules.getId(), List.of(expRuleToBeFound)));
        CategoryDTO childWithQuantityRule = categoryService.save(new CategoryCreateDTO("childWithQuantityRule", parentAndChildWithExpirationRule.getId(), List.of(quantRuleToBeFound)));
        Set<RuleType> ruleTypesToLookFor = Stream.of(RuleType.QUANT, RuleType.EXP).collect(Collectors.toSet());

        List<RuleCreateDTO> applicableRules = categoryService.findApplicableRules(childWithQuantityRule.getId(), ruleTypesToLookFor);
        long totalMatches = applicableRules.stream()
                .filter(rule -> Objects.equals(rule, childWithQuantityRule.getRules().get(0)) || Objects.equals(rule, parentAndChildWithExpirationRule.getRules().get(0))).count();
        Assertions.assertEquals(2, applicableRules.size());
        Assertions.assertEquals(2, totalMatches);
    }

}
