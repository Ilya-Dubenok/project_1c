package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.product.ItemDTO;
import org.example.core.dto.product.ProductCreateDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.core.exception.InternalException;
import org.example.core.exception.EntityNotFoundException;
import org.example.dao.entities.product.IRule;
import org.example.dao.entities.product.Item;
import org.example.dao.entities.product.Product;
import org.example.dao.entities.product.RuleType;
import org.example.dao.repository.IProductRepository;
import org.example.service.api.ICategoryClient;
import org.example.service.api.IProductService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ModelMapper mapper;

    private final IProductRepository productRepository;

    private final ICategoryClient categoryClient;

    @Override
    public ProductDTO save(ProductCreateDTO productCreateDTO) {
        UUID categoryId = productCreateDTO.getCategoryId();
        verifyCategoryUuid(categoryId);
        Set<IRule> rules = formSetOfRules(productCreateDTO.getRules());
        List<Item> items = formListOfItems(productCreateDTO.getItems());
        Product product = new Product(UUID.randomUUID(), categoryId, productCreateDTO.getName(), rules, items);
        return mapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public ProductDTO findByUUID(UUID uuid) {
        Product product = getProductOrThrow(uuid);
        return mapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO findByName(String name) {
        Product product = productRepository.findByName(name.toLowerCase()).orElseThrow(() -> new EntityNotFoundException("product"));
        return mapper.map(product, ProductDTO.class);
    }

    @Override
    public Page<ProductDTO> getPage(Pageable pageable) {
        Page<Product> pageOfProducts = productRepository.findAll(pageable);
        return pageOfProducts.map(category -> mapper.map(category, ProductDTO.class));
    }

    @Override
    public ProductDTO updateName(UUID productUuid, String name) {
        Product product = getProductOrThrow(productUuid);
        product.setName(name);
        return mapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public ProductDTO updateRules(UUID productUuid, List<RuleDTO> ruleDTOList) {
        Product product = getProductOrThrow(productUuid);
        Set<IRule> ruleSet = formSetOfRules(ruleDTOList);
        product.setRules(ruleSet);
        return mapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public ProductDTO updateItems(UUID productUuid, List<ItemDTO> itemDTOList) {
        Product product = getProductOrThrow(productUuid);
        List<Item> items = formListOfItems(itemDTOList);
        product.setItems(items);
        return mapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public ProductDTO addItem(UUID productUuid, ItemDTO itemDTO) {
        Product product = getProductOrThrow(productUuid);
        Optional<Item> sameItemForExpiresAt = findItemFromProductWhichExpiresAt(product, itemDTO.getExpiresAt());

        sameItemForExpiresAt.ifPresentOrElse(
                persistedItem -> persistedItem.setQuantity(persistedItem.getQuantity() + itemDTO.getQuantity()),
                () -> product.getItems().add(mapper.map(itemDTO, Item.class))
        );
        return mapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public ProductDTO addToItemQuantity(UUID productUuid, LocalDate expiresAt, Integer summand) {
        Product product = getProductOrThrow(productUuid);
        Item item = findItemFromProductWhichExpiresAt(product, expiresAt)
                .orElseThrow(() -> new InternalException("no item found for this expiration date"));

        int totalAmount = item.getQuantity() + summand;
        if (totalAmount <= 0) {
            product.getItems().remove(item);
        } else {
            item.setQuantity(totalAmount);
        }
        return mapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public ProductDTO changeItemExpirationDate(UUID productUuid, LocalDate expiresAt, LocalDate replacement) {
        Product product = getProductOrThrow(productUuid);
        Item item = findItemFromProductWhichExpiresAt(product, expiresAt)
                .orElseThrow(() -> new InternalException("no item found for this expiration date"));
        item.setExpiresAt(replacement);
        mergeItemsWithSameExpirationDate(product.getItems(), item);
        return mapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public void delete(UUID productUuid) {
        if (!productRepository.existsById(productUuid)) {
            throw new EntityNotFoundException("product");
        }
        productRepository.deleteById(productUuid);
    }

    private void mergeItemsWithSameExpirationDate(List<Item> items, Item probeItem) {
        if (null != items) {
            findAnyItemByFilter(items, item -> item != probeItem && Objects.equals(item.getExpiresAt(), probeItem.getExpiresAt()))
                    .ifPresent(duplicateItem -> {
                        probeItem.setQuantity(probeItem.getQuantity() + duplicateItem.getQuantity());
                        items.remove(duplicateItem);
                    });
        }
    }

    private void verifyCategoryUuid(UUID categoryId) {
        if (!categoryClient.categoryExists(categoryId)) {
            throw new InternalException("specified category does not exist");
        }
    }

    private Product getProductOrThrow(UUID productUuid) {
        return productRepository.findById(productUuid).orElseThrow(() -> new EntityNotFoundException("product"));
    }

    private List<Item> formListOfItems(List<ItemDTO> itemDTOList) {
        if (null == itemDTOList || itemDTOList.size() == 0) {
            return new ArrayList<>();
        }
        Map<LocalDate, Item> itemMap = itemDTOList.stream()
                .map(itemDTO -> mapper.map(itemDTO, Item.class))
                .collect(Collectors.toMap(Item::getExpiresAt, Function.identity(), (item1, item2) -> {
                    item1.setQuantity(item1.getQuantity() + item2.getQuantity());
                    return item1;
                }));
        return new ArrayList<>(itemMap.values());
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

    private static Optional<Item> findItemFromProductWhichExpiresAt(Product product, LocalDate expiresAt) {
        return product.getItems().stream()
                .filter(i -> Objects.equals(i.getExpiresAt(), expiresAt))
                .findAny();
    }

    private static Optional<Item> findAnyItemByFilter(Collection<Item> items, Predicate<Item> filterPredicate) {
        return items.stream()
                .filter(filterPredicate)
                .findAny();
    }
}
