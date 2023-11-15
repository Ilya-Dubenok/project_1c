package org.example.service.api;

import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.core.dto.rule.RuleType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

//TODO add fallback
@FeignClient(name = "category-service", path = "/internal")
public interface ICategoryClient {

    @RequestMapping(method = RequestMethod.GET, path = "/exists/{uuid}")
    Boolean categoryExists(@PathVariable(name = "uuid") UUID categoryUuid);

    @RequestMapping(value = "/applicable_rules/{uuid}")
    List<RuleDTO> getRulesApplicableFromCategories(@PathVariable(name = "uuid") UUID categoryUuid, @RequestParam(name = "rules") Set<RuleType> ruleTypeSet);

    @GetMapping(value = "/category_and_parents/{uuid}")
    List<CategoryDTO> getCategoryAndParents(@PathVariable(name = "uuid") UUID categoryUuid);
}
