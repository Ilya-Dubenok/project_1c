package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.product.ItemDTO;
import org.example.core.dto.product.ProductCreateDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.core.exception.InternalException;
import org.example.dao.entities.product.IRule;
import org.example.dao.entities.product.Item;
import org.example.dao.entities.product.Product;
import org.example.dao.entities.product.RuleType;
import org.example.dao.repository.IProductRepository;
import org.example.service.api.CategoryClient;
import org.example.service.api.IProductService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ModelMapper mapper;

    private final IProductRepository productRepository;

    private final CategoryClient categoryClient;

    @Override
    public ProductDTO save(ProductCreateDTO productCreateDTO) {
        UUID categoryId = productCreateDTO.getCategoryId();
        verifyCategoryUuid(categoryId);
        Set<IRule> rules = formSetOfRules(productCreateDTO.getRules());
        List<Item> items = formListOfItems(productCreateDTO.getItems());
        Product product = new Product(UUID.randomUUID(), categoryId, productCreateDTO.getName(), rules, items);
        return mapper.map(productRepository.save(product), ProductDTO.class);
    }

    private List<Item> formListOfItems(List<ItemDTO> itemDTOList) {
        return itemDTOList == null ? new ArrayList<>() : itemDTOList.stream().map(itemDTO -> mapper.map(itemDTO, Item.class)).toList();
    }

    private Set<IRule> formSetOfRules(List<RuleDTO> ruleDTOList) {
        if (null == ruleDTOList) {
            return new HashSet<>();
        }
        List<RuleType> ruleTypesLeft = new ArrayList<>(List.of(RuleType.values()));

        return ruleDTOList.stream()
                .takeWhile(rule -> ruleTypesLeft.size() > 0)
                .map(rule -> mapper.map(rule, IRule.class))
                .filter(rule -> ruleTypesLeft.contains(rule.getRuleType()))
                .peek(rule -> ruleTypesLeft.remove(rule.getRuleType()))
                .collect(Collectors.toSet());
    }

    @Override
    public ProductDTO findByUUID(UUID uuid) {
        return null;
    }

    @Override
    public ProductDTO findByName(String name) {
        return null;
    }

    @Override
    public Page<ProductDTO> getPage(Pageable pageable) {
        return null;
    }

    @Override
    public ProductDTO updateName(UUID productUuid, String name) {
        return null;
    }

    @Override
    public ProductDTO updateRules(UUID productUuid, List<RuleDTO> rules) {
        return null;
    }

    @Override
    public ProductDTO updateItems(UUID productUuid, List<ItemDTO> items) {
        return null;
    }

    @Override
    public ProductDTO changeItem(UUID productUuid, String itemName, ItemDTO replacement) {
        return null;
    }

    @Override
    public ProductDTO addToItemQuantity(UUID productUuid, String itemName, Integer summand) {
        return null;
    }

    @Override
    public ProductDTO changeItemExpirationDate(UUID productUuid, String itemName, LocalDate replacement) {
        return null;
    }

    @Override
    public void delete(UUID productUuid) {

    }

    private void verifyCategoryUuid(UUID categoryId) {
        if (!categoryClient.categoryExists(categoryId)) {
            throw new InternalException("specified category does not exist");
        }
    }
}
