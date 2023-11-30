package org.example.enpoint.web;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.rule.RuleCreateDTO;
import org.example.dao.entities.RuleType;
import org.example.service.api.ICategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Hidden
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/internal")
public class InternalRequestsController {

    private final ICategoryService categoryService;

    @GetMapping(value = "/exists/{id}")
    public Boolean categoryExists(@PathVariable UUID id) {
        return categoryService.existsById(id);
    }

    @GetMapping(value = "/applicable_rules/{id}")
    public List<RuleCreateDTO> getRulesStartingFromCategory(@PathVariable(name = "id") UUID categoryId, @RequestParam(name = "rules") Set<RuleType> ruleTypeSet) {
        return categoryService.findApplicableRules(categoryId, ruleTypeSet);
    }

    @GetMapping(value = "/category_and_parents/{id}")
    public List<CategoryDTO> findCategoryAndParents(@PathVariable(name = "id") UUID categoryId){
        return categoryService.findCategoryAndParents(categoryId);
    }
}
