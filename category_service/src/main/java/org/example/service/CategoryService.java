package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.category.CategoryCreateDTO;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.category.CategoryUpdateDTO;
import org.example.core.exception.CategoryNotFoundException;
import org.example.core.exception.InternalException;
import org.example.dao.entities.Category;
import org.example.dao.repositories.ICategoryRepository;
import org.example.core.dto.rule.RuleCreateDTO;
import org.example.dao.entities.IRule;
import org.example.dao.entities.RuleType;
import org.example.service.api.ICategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final ICategoryRepository categoryRepository;

    private final ModelMapper mapper;

    @Override
    public CategoryDTO save(CategoryCreateDTO categoryCreateDTO) {
        String categoryName = getValidatedCategoryName(categoryCreateDTO);
        Category parent = getValidatedCategoryParent(categoryCreateDTO);
        List<IRule> rules = createRuleList(categoryCreateDTO.getRules());
        Category categoryToSave = new Category(UUID.randomUUID(), categoryName, parent, rules);
        return mapper.map(categoryRepository.save(categoryToSave), CategoryDTO.class);
    }

    @Override
    public CategoryDTO findByUUID(UUID uuid) {
        Category category = categoryRepository.findById(uuid).orElseThrow(CategoryNotFoundException::new);
        return mapper.map(category, CategoryDTO.class);
    }

    @Override
    public Page<CategoryDTO> getPage(Pageable pageable) {
        Page<Category> pageOfCategories = categoryRepository.findAll(pageable);
        return pageOfCategories.map(category -> mapper.map(category, CategoryDTO.class));
    }

    @Override
    public List<CategoryDTO> findChildrenByParentId(UUID parentUUID) {
        return categoryRepository.findByParent_UuidEquals(parentUUID).stream()
                .map(x->mapper.map(x, CategoryDTO.class))
                .toList();
    }

    @Override
    public Map<RuleType, CategoryDTO> findCategoriesForRules(UUID startCategoryId, Set<RuleType> types) {
        //TODO ADD LOGIC WHEN PRODUCT SERVICE IS DEFINED
        return null;
    }

    @Override
    public CategoryDTO updateNameAndRules(UUID uuid, CategoryUpdateDTO categoryUpdateDTO) {

        Category categoryToUpdate = categoryRepository.findById(uuid).orElseThrow(CategoryNotFoundException::new);
        categoryToUpdate.setName(categoryUpdateDTO.getName());
        List<IRule> rules = createRuleList(categoryUpdateDTO.getRules());
        categoryToUpdate.setRules(rules);
        Category savedCategory = categoryRepository.save(categoryToUpdate);
        return mapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public void delete(UUID uuid) {
        if (!categoryRepository.existsById(uuid)) {
            throw new CategoryNotFoundException();
        }
        categoryRepository.deleteById(uuid);
    }

    private String getValidatedCategoryName(CategoryCreateDTO categoryCreateDTO) {
        String categoryName = categoryCreateDTO.getName();
        if (categoryRepository.findByName(categoryName) != null) {
            throw new InternalException("This name is already present");
        }
        return categoryName;
    }

    private Category getValidatedCategoryParent(CategoryCreateDTO categoryCreateDTO) {
        UUID parentUuid = categoryCreateDTO.getParentUuid();
        Category parent = null;
        if (null != parentUuid) {
            parent = categoryRepository.findById(parentUuid).orElseThrow(
                    () -> new InternalException("No category for this parent uuid found")
            );
        }
        return parent;
    }

    private List<IRule> createRuleList(List<RuleCreateDTO> listOfRuleCreateDTO) {

        List<RuleType> ruleTypesLeft = new ArrayList<>(Arrays.stream(RuleType.values()).toList());

        return listOfRuleCreateDTO.stream()
                .takeWhile(ruleCreateDTO -> ruleTypesLeft.size() > 0)
                .map(ruleCreateDTO ->  mapper.map(ruleCreateDTO, IRule.class))
                .filter(x->ruleTypesLeft.contains(x.getRuleType()))
                .peek(x -> ruleTypesLeft.remove(x.getRuleType()))
                .toList();
    }
}
