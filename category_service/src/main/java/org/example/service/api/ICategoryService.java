package org.example.service.api;

import jakarta.validation.Valid;
import org.example.core.dto.CategoryCreateDTO;
import org.example.core.dto.CategoryUpdateDTO;
import org.example.dao.entities.Category;
import org.example.entities.RuleType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ICategoryService {

    Category save(@Valid CategoryCreateDTO categoryCreateDTO);

    Category findByUUID(UUID uuid);

    List<Category> findChildrenByParentId(UUID parentUUID);

    Page<Category> getPage(Integer currentRequestedPage, Integer rowsPerPage);

    List<Category> findCategoryForRules(UUID startCategoryId, Set<RuleType> types);

    Category updateNameAndRules(UUID uuid, @Valid CategoryUpdateDTO categoryUpdateDTO);

    void delete(UUID uuid);


}
