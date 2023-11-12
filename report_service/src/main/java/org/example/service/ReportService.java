package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.report.PendingProductDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.core.dto.rule.RuleType;
import org.example.service.api.ICategoryClient;
import org.example.service.api.IProductClient;
import org.example.service.api.IReportService;
import org.example.service.transitional.IRule;
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
    public List<PendingProductDTO> getListOfPendingProducts() {
        List<ProductDTO> productsList = productClient.getProductsList();
        return null;
    }

    //TODO change to private when finished
    public Map<ProductDTO, Integer> getMapOfProductsToBuy(ProductDTO productDTO) {
        Map<ProductDTO, Integer> mapOfProductsToBuy = new HashMap<>();
        Set<RuleType> ruleTypesToApply = new HashSet<>(List.of(RuleType.values()));
        fillMapByApplicableProductRules(mapOfProductsToBuy, productDTO, ruleTypesToApply);
        if (ruleTypesToApply.size() > 0) {
            fillMapByApplicableCategoryRules(mapOfProductsToBuy, productDTO, ruleTypesToApply);
        }
        return mapOfProductsToBuy;
    }

    private void fillMapByApplicableProductRules(Map<ProductDTO, Integer> mapOfProductsToFill, ProductDTO productDTO, Set<RuleType> ruleTypesToApply) {
        List<RuleDTO> productRules = productDTO.getRules();
        if (null != productRules) {
            fillMapByListOfRuleDTOs(mapOfProductsToFill, productDTO, ruleTypesToApply, productRules);
        }
    }

    private void fillMapByApplicableCategoryRules(Map<ProductDTO, Integer> mapOfProductsToBuy, ProductDTO productDTO, Set<RuleType> ruleTypesToApply) {
        List<RuleDTO> rulesApplicableFromCategories = categoryClient.getRulesApplicableFromCategories(productDTO.getCategoryId(), ruleTypesToApply);
        fillMapByListOfRuleDTOs(mapOfProductsToBuy, productDTO, ruleTypesToApply, rulesApplicableFromCategories);
    }

    private void fillMapByListOfRuleDTOs(Map<ProductDTO, Integer> mapOfProductsToFill, ProductDTO productDTO, Set<RuleType> ruleTypesToApply, List<RuleDTO> rulesList) {
        rulesList.stream()
                .map(ruleDTO -> mapper.map(ruleDTO, IRule.class))
                .peek(iRule -> ruleTypesToApply.remove(iRule.getRuleType()))
                .map(iRule -> iRule.getQuantityOfProductsToBuy(productDTO))
                .filter(quantityToBuy -> quantityToBuy > 0)
                .forEach(quantityToBuy -> mapOfProductsToFill.merge(productDTO, quantityToBuy, Integer::sum));
    }

}
