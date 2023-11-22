package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.exception.EntityNotFoundException;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.report.ProductToBuyDTO;
import org.example.core.dto.report.ReportDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.core.dto.rule.RuleType;
import org.example.dao.entities.Report;
import org.example.dao.entities.ReportData;
import org.example.dao.repository.IReportRepository;
import org.example.service.api.ICategoryClient;
import org.example.service.api.IProductClient;
import org.example.service.api.IReportService;
import org.example.service.transitional.IRule;
import org.example.service.transitional.NodeChain;
import org.example.service.transitional.ProductToBuy;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService {

    private final ModelMapper mapper;

    private final ICategoryClient categoryClient;

    private final IProductClient productClient;

    private final IReportRepository reportRepository;

    @Override
    public List<ProductToBuyDTO> getProductsToBuyDTO() {
        return getListOfProductsToBuy().stream()
                .map(productToBuy -> mapper.map(productToBuy, ProductToBuyDTO.class))
                .toList();
    }

    @Override
    public ReportDTO formReport() {
        List<ReportData> data = formReportData();
        Report report = new Report(UUID.randomUUID(), LocalDateTime.now(), data);
        return mapper.map(reportRepository.save(report), ReportDTO.class);
    }

    @Override
    public ReportDTO gerReport(UUID uuid) {
        Report report = reportRepository.findById(uuid).orElseThrow(() -> new EntityNotFoundException("report"));
        return mapper.map(report, ReportDTO.class);
    }

    private List<ReportData> formReportData() {
        List<ProductToBuy> listOfProductsToBuy = getListOfProductsToBuy();
        if (null == listOfProductsToBuy || listOfProductsToBuy.isEmpty()) {
            return new ArrayList<>();
        }
        List<NodeChain> nodeChainList = formListOfNodeChains(new ArrayList<>(listOfProductsToBuy));
        return nodeChainList.stream()
                .map(nodeChain -> mapper.map(nodeChain.getTopNode(), ReportData.class))
                .toList();
    }

    private List<NodeChain> formListOfNodeChains(List<ProductToBuy> listOfProductsToBuy) {
        List<NodeChain> nodeChainList = new ArrayList<>();
        nodeChainList.add(new NodeChain(listOfProductsToBuy.remove(0)));
        while (listOfProductsToBuy.size() > 0) {
            ProductToBuy productToBuy = listOfProductsToBuy.remove(0);
            if (!productToBuyIsMergedIntoNodeChain(nodeChainList, productToBuy)) {
                nodeChainList.add(new NodeChain(productToBuy));
            }
        }
        return nodeChainList;
    }

    private boolean productToBuyIsMergedIntoNodeChain(List<NodeChain> nodeChainList, ProductToBuy productToBuy) {
        return nodeChainList.stream().anyMatch(nodeChain -> nodeChain.isProductToBuyMergedIntoChain(productToBuy));
    }

    private List<ProductToBuy> getListOfProductsToBuy() {
        return productClient.getProductsList().stream()
                .map(this::formProductToBuyWithGreaterThanZeroQuantity)
                .filter(Optional::isPresent)
                .map(Optional::get)
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
