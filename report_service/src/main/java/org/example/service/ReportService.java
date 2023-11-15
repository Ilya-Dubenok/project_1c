package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.report.ProductToBuyDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.core.dto.rule.RuleType;
import org.example.service.api.ICategoryClient;
import org.example.service.api.IProductClient;
import org.example.service.api.IReportService;
import org.example.service.transitional.IRule;
import org.example.service.transitional.ProductToBuy;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService {

    private final ModelMapper mapper;

    private final ICategoryClient categoryClient;

    private final IProductClient productClient;

    @Override
    public List<ProductToBuyDTO> getProductsToBuy() {
        return productClient.getProductsList().stream()
                .map(this::formProductToBuyWithGreaterThanZeroQuantity)
                .filter(Optional::isPresent)
                .map(optionalProductToBuy -> mapper.map(optionalProductToBuy.get(), ProductToBuyDTO.class))
                .toList();
    }

    private ProductToBuy formProductToBuy(ProductDTO productDTO) {
        ProductToBuy productToBuy = new ProductToBuy(productDTO);
        defineQuantityToBuy(productToBuy);
        setCategories(productToBuy);
        return productToBuy;
    }

    private Optional<ProductToBuy> formProductToBuyWithGreaterThanZeroQuantity(ProductDTO productDTO) {
        ProductToBuy productToBuy = new ProductToBuy(productDTO);
        defineQuantityToBuy(productToBuy);
        if (productToBuy.getQuantity() > 0) {
            setCategories(productToBuy);
            return Optional.of(productToBuy);
        } else {
            return Optional.empty();
        }
    }

    private void setCategories(ProductToBuy productToBuy) {
        UUID categoryUuid = productToBuy.getProductDTO().getCategoryId();
        List<CategoryDTO> categoryList = categoryClient.getCategoryAndParents(categoryUuid);
        productToBuy.setCategories(categoryList);
    }

    private void defineQuantityToBuy(ProductToBuy productToBuy) {
        Set<RuleType> ruleTypesToApply = new HashSet<>(List.of(RuleType.values()));
        applyProductRules(productToBuy, ruleTypesToApply);
        if (ruleTypesToApply.size() > 0) {
            applyCategoryRules(productToBuy, ruleTypesToApply);
        }
    }

    private void applyProductRules(ProductToBuy productToBuy, Set<RuleType> ruleTypesToApply) {
        List<RuleDTO> productRules = productToBuy.getProductDTO().getRules();
        if (null != productRules) {
            defineQuantityToBuy(productToBuy, ruleTypesToApply, productRules);
        }
    }

    private void applyCategoryRules(ProductToBuy productToBuy, Set<RuleType> ruleTypesToApply) {
        List<RuleDTO> rulesApplicableFromCategories = categoryClient.getRulesApplicableFromCategories(productToBuy.getProductDTO().getCategoryId(), ruleTypesToApply);
        defineQuantityToBuy(productToBuy, ruleTypesToApply, rulesApplicableFromCategories);
    }

    private void defineQuantityToBuy(ProductToBuy productToBuy, Set<RuleType> ruleTypesToApply, List<RuleDTO> rulesList) {
        rulesList.stream()
                .map(ruleDTO -> mapper.map(ruleDTO, IRule.class))
                .peek(iRule -> ruleTypesToApply.remove(iRule.getRuleType()))
                .map(iRule -> iRule.getQuantityOfProductsToBuy(productToBuy.getProductDTO()))
                .filter(quantityToBuy -> quantityToBuy > 0)
                .forEach(productToBuy::addQuantity);
    }

}
