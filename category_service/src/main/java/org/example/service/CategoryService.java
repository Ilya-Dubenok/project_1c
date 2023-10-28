package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.PageDTO;
import org.example.core.dto.category.CategoryCreateDTO;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.category.CategoryUpdateDTO;
import org.example.core.exception.InternalException;
import org.example.dao.entities.Category;
import org.example.dao.repositories.ICategoryRepository;
import org.example.core.dto.rule.ExpirationRuleCreateDTO;
import org.example.core.dto.rule.RuleCreateDTO;
import org.example.core.dto.rule.QuantityRuleCreateDTO;
import org.example.dao.entities.ExpirationRule;
import org.example.dao.entities.IRule;
import org.example.dao.entities.QuantityRule;
import org.example.dao.entities.RuleType;
import org.example.service.api.ICategoryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.Type;
import java.util.*;

@Service
@Validated
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private static final String NO_CATEGORY_FOUND_MESSAGE = "no category for the provided uuid found";

    private final ICategoryRepository categoryRepository;

    private final ModelMapper mapper;


    @Override
    public Category save(CategoryCreateDTO categoryCreateDTO) {

        String categoryName = categoryCreateDTO.getName();

        if (categoryRepository.findByName(categoryName) != null) {
            throw new InternalException("This name is already present");
        }

        UUID parentUuid = categoryCreateDTO.getParentUuid();

        Category parent = null;

        if (null != parentUuid) {
            parent = categoryRepository.findById(parentUuid).orElseThrow(
                    () -> new InternalException("No category for this parent uuid found")
            );
        }

        List<IRule> rules = createRuleList(categoryCreateDTO.getRules());

        Category toPersist = new Category(UUID.randomUUID(), categoryName, parent, rules);

        return categoryRepository.save(toPersist);
    }

    @Override
    public Category findByUUID(UUID uuid) {
        return categoryRepository.findById(uuid).orElse(null);
    }

    @Override
    public Page<CategoryDTO> getPage(Pageable pageable) {
        Page<Category> pageOfCategories = categoryRepository.findAll(pageable);
        return pageOfCategories.map(category -> mapper.map(category, CategoryDTO.class));
    }

    @Override
    public List<Category> findChildrenByParentId(UUID parentUUID) {
        return categoryRepository.findByParent_UuidEquals(parentUUID);
    }

    @Override
    public Map<RuleType, Category> findCategoriesForRules(UUID startCategoryId, Set<RuleType> types) {
        //TODO ADD LOGIC WHEN PRODUCT SERVICE IS DEFINED
        return null;
    }

    @Override
    public Category updateNameAndRules(UUID uuid, CategoryUpdateDTO categoryUpdateDTO) {

        Category toUpdate = categoryRepository.findById(uuid).orElseThrow(
                () -> new InternalException(NO_CATEGORY_FOUND_MESSAGE)
        );

        toUpdate.setName(categoryUpdateDTO.getName());

        List<IRule> rules = createRuleList(categoryUpdateDTO.getRules());

        toUpdate.setRules(rules);

        return categoryRepository.save(toUpdate);
    }

    @Override
    public void delete(UUID uuid) {

        if (!categoryRepository.existsById(uuid)) {
            throw new InternalException(NO_CATEGORY_FOUND_MESSAGE);
        }

        categoryRepository.deleteById(uuid);
    }

    private List<IRule> createRuleList(List<RuleCreateDTO> rules) {

        if (rules == null) {
            return null;
        }

        List<IRule> res = new ArrayList<>();

        List<RuleType> ruleTypesLeft = new ArrayList<>(Arrays.stream(RuleType.values()).toList());

        for (RuleCreateDTO ruleCreateDTO : rules) {

            if (ruleTypesLeft.size() < 1) {
                break;
            }

            IRule iRule = null;

            UUID randomUUID = UUID.randomUUID();

            if (ruleCreateDTO instanceof QuantityRuleCreateDTO) {
                iRule = new QuantityRule(randomUUID, ((QuantityRuleCreateDTO) ruleCreateDTO).getMinimumQuantity());
                ruleTypesLeft.remove(RuleType.QUANT);

            } else if (ruleCreateDTO instanceof ExpirationRuleCreateDTO) {
                iRule = new ExpirationRule(randomUUID, ((ExpirationRuleCreateDTO) ruleCreateDTO).getDaysTillExpiration());
                ruleTypesLeft.remove(RuleType.EXP);
            }

            res.add(iRule);

        }

        return res;
    }
}
