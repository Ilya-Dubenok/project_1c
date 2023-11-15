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

    @GetMapping(value = "/exists/{uuid}")
    public Boolean categoryExists(@PathVariable UUID uuid) {
        return categoryService.existsByUuid(uuid);
    }

    @GetMapping(value = "/applicable_rules/{uuid}")
    public List<RuleCreateDTO> getRulesStartingFromCategory(@PathVariable(name = "uuid") UUID categoryUuid, @RequestParam(name = "rules") Set<RuleType> ruleTypeSet) {
        return categoryService.findApplicableRules(categoryUuid, ruleTypeSet);
    }

    @GetMapping(value = "/category_and_parents/{uuid}")
    public List<CategoryDTO> findCategoryAndParents(@PathVariable(name = "uuid") UUID categoryUuid){
        return categoryService.findCategoryAndParents(categoryUuid);
    }
}
