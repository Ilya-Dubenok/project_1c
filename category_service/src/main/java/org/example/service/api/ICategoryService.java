package org.example.service.api;

import jakarta.validation.Valid;
import org.example.core.dto.category.CategoryCreateDTO;
import org.example.core.dto.category.CategoryUpdateDTO;
import org.example.dao.entities.Category;
import org.example.dao.entities.RuleType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ICategoryService {

    Category save(@Valid CategoryCreateDTO categoryCreateDTO);

    Category findByUUID(UUID uuid);

    List<Category> findChildrenByParentId(UUID parentUUID);

    Page<Category> getPage(Integer currentRequestedPage, Integer rowsPerPage);

    Map<RuleType, Category> findCategoriesForRules(UUID startCategoryId, Set<RuleType> types);

    Category updateNameAndRules(UUID uuid, @Valid CategoryUpdateDTO categoryUpdateDTO);

    void delete(UUID uuid);


}
