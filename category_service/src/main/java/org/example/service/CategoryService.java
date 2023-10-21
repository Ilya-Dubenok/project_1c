package org.example.service;

import org.example.core.dto.CategoryCreateDTO;
import org.example.core.dto.CategoryUpdateDTO;
import org.example.dao.entities.Category;
import org.example.entities.RuleType;
import org.example.service.api.ICategoryService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Validated
public class CategoryService implements ICategoryService {


    @Override
    public Category save(CategoryCreateDTO categoryCreateDTO) {
        return null;
    }

    @Override
    public Category findByUUID(UUID uuid) {
        return null;
    }

    @Override
    public Page<Category> getPage(Integer currentRequestedPage, Integer rowsPerPage) {
        return null;
    }

    @Override
    public List<Category> findChildrenByParentId(UUID parentUUID) {
        return null;
    }

    @Override
    public List<Category> findCategoryForRules(UUID startCategoryId, Set<RuleType> types) {
        return null;
    }

    @Override
    public Category updateNameAndRules(UUID uuid, CategoryUpdateDTO categoryUpdateDTO) {
        return null;
    }

    @Override
    public void delete(UUID uuid) {

    }
}
