package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.category.CategoryCreateDTO;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.category.CategoryUpdateDTO;
import org.example.core.exception.EntityNotFoundException;
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
    public CategoryDTO findById(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("category"));
        return mapper.map(category, CategoryDTO.class);
    }

    @Override
    public Page<CategoryDTO> getPage(Pageable pageable) {
        Page<Category> pageOfCategories = categoryRepository.findAll(pageable);
        return pageOfCategories.map(category -> mapper.map(category, CategoryDTO.class));
    }

    @Override
    public List<CategoryDTO> findChildrenByParentId(UUID parentId) {
        return categoryRepository.findByParent_IdEquals(parentId).stream()
                .map(x -> mapper.map(x, CategoryDTO.class))
                .toList();
    }

    @Override
    public List<CategoryDTO> findCategoryAndParents(UUID categoryId) {
        List<CategoryDTO> parentsList = new ArrayList<>();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("category"));
        parentsList.add(mapper.map(category, CategoryDTO.class));
        parentsList.addAll(getAllParentsForCategoryAsDTOs(category));
        return parentsList;
    }

    @Override
    public List<RuleCreateDTO> findApplicableRules(UUID startCategoryId, Set<RuleType> ruleTypes) {
        Category category = categoryRepository.findById(startCategoryId).orElseThrow(() -> new EntityNotFoundException("category"));
        List<RuleCreateDTO> listOfApplicableRules = new ArrayList<>();
        while (ruleTypes.size() > 0 && null != category) {
            listOfApplicableRules.addAll(getListOfMatchingRulesForCategory(ruleTypes, category));
            category = category.getParent();
        }
        return listOfApplicableRules;
    }

    @Override
    public CategoryDTO updateNameAndRules(UUID id, CategoryUpdateDTO categoryUpdateDTO) {

        Category categoryToUpdate = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("category"));
        categoryToUpdate.setName(categoryUpdateDTO.getName());
        List<IRule> rules = createRuleList(categoryUpdateDTO.getRules());
        categoryToUpdate.setRules(rules);
        Category savedCategory = categoryRepository.save(categoryToUpdate);
        return mapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Boolean existsById(UUID id) {
        return categoryRepository.existsById(id);
    }

    private List<CategoryDTO> getAllParentsForCategoryAsDTOs(Category category) {
        Category parent = category.getParent();
        List<CategoryDTO> parentsList = new ArrayList<>();
        while (null != parent){
            parentsList.add(mapper.map(parent, CategoryDTO.class));
            parent = parent.getParent();
        }
        return parentsList;
    }

    private String getValidatedCategoryName(CategoryCreateDTO categoryCreateDTO) {
        String categoryName = categoryCreateDTO.getName();
        if (categoryRepository.findByName(categoryName) != null) {
            throw new InternalException("This name is already present");
        }
        return categoryName;
    }

    private Category getValidatedCategoryParent(CategoryCreateDTO categoryCreateDTO) {
        UUID parentId = categoryCreateDTO.getParentId();
        Category parent = null;
        if (null != parentId) {
            parent = categoryRepository.findById(parentId).orElseThrow(
                    () -> new InternalException("No category for this parent id found")
            );
        }
        return parent;
    }

    private List<IRule> createRuleList(List<RuleCreateDTO> listOfRuleCreateDTO) {
        if (null == listOfRuleCreateDTO) {
            return new ArrayList<>();
        }
        List<RuleType> ruleTypesLeft = new ArrayList<>(List.of(RuleType.values()));

        return listOfRuleCreateDTO.stream()
                .takeWhile(ruleCreateDTO -> ruleTypesLeft.size() > 0)
                .map(ruleCreateDTO -> mapper.map(ruleCreateDTO, IRule.class))
                .filter(x -> ruleTypesLeft.contains(x.getRuleType()))
                .peek(x -> ruleTypesLeft.remove(x.getRuleType()))
                .toList();
    }

    private List<RuleCreateDTO> getListOfMatchingRulesForCategory(Set<RuleType> ruleTypeSet, Category category) {
        return category.getRules()
                .stream()
                .filter(iRule -> ruleTypeSet.remove(iRule.getRuleType()))
                .map(iRule -> mapper.map(iRule, RuleCreateDTO.class))
                .toList();
    }
}
