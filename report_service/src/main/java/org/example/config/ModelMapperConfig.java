package org.example.config;

import org.example.core.dto.report.ProductToBuyDTO;
import org.example.core.dto.report.ReportDataDTO;
import org.example.core.dto.rule.ExpirationRuleDTO;
import org.example.core.dto.rule.QuantityRuleDTO;
import org.example.dao.entities.ProductData;
import org.example.dao.entities.ReportData;
import org.example.service.transitional.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper
                .createTypeMap(ExpirationRuleDTO.class, IRule.class)
                .setProvider(context -> {
                    ExpirationRuleDTO source = (ExpirationRuleDTO) context.getSource();
                    ExpirationRule expirationRule = new ExpirationRule();
                    expirationRule.setDaysTillExpiration(source.getDaysTillExpiration());
                    return expirationRule;
                });

        modelMapper
                .createTypeMap(QuantityRuleDTO.class, IRule.class)
                .setProvider(context -> {
                    QuantityRuleDTO source = (QuantityRuleDTO) context.getSource();
                    QuantityRule quantityRule = new QuantityRule();
                    quantityRule.setMinimumQuantity(source.getMinimumQuantity());
                    return quantityRule;
                });

        modelMapper
                .createTypeMap(ProductToBuy.class, ProductToBuyDTO.class)
                .addMappings(new PropertyMap<>() {
                    @Override
                    protected void configure() {
                        map().setUuid(source.getProductDTO().getUuid());
                        map().setName(source.getProductDTO().getName());
                    }
                });

        modelMapper.createTypeMap(Node.class, ReportData.class)
                .addMappings(mapping -> mapping.using(fromListOfProductToBuyToListOfProductDataConverter()).map(Node::getProducts, ReportData::setProducts))
                .addMappings(mapping -> mapping.using(fromSetOfNodesToListOfReportDataConverter()).map(Node::getChildren, ReportData::setInnerDataList));

        modelMapper.createTypeMap(ReportData.class, ReportDataDTO.class)
                .addMapping(ReportData::getInnerDataList, ReportDataDTO::setSubcategories);

        return modelMapper;
    }

    private Converter<List<ProductToBuy>, List<ProductData>> fromListOfProductToBuyToListOfProductDataConverter() {
       return context -> context.getSource().stream()
               .map(productToBuy -> new ProductData(productToBuy.getProductDTO().getUuid(), productToBuy.getProductDTO().getName(), productToBuy.getQuantity())).toList();
    }

    private Converter<Set<Node>,List<ReportData>> fromSetOfNodesToListOfReportDataConverter() {
        return context -> context.getSource().stream()
                .map(node -> modelMapper().map(node, ReportData.class)).toList();
    }

}
