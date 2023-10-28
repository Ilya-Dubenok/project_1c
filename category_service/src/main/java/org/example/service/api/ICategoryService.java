package org.example.service.api;

import org.example.core.dto.category.CategoryCreateDTO;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.category.CategoryUpdateDTO;
import org.example.dao.entities.Category;
import org.example.dao.entities.RuleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ICategoryService {

    Category save(CategoryCreateDTO categoryCreateDTO);

    Category findByUUID(UUID uuid);

    List<Category> findChildrenByParentId(UUID parentUUID);

    Page<CategoryDTO> getPage(Pageable pageable);

    Map<RuleType, Category> findCategoriesForRules(UUID startCategoryId, Set<RuleType> types);

    Category updateNameAndRules(UUID uuid, CategoryUpdateDTO categoryUpdateDTO);

    void delete(UUID uuid);


}
