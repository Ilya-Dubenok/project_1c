package org.example.config;

import lombok.NonNull;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.report.ProductToBuyDTO;
import org.example.core.dto.rule.ExpirationRuleDTO;
import org.example.core.dto.rule.QuantityRuleDTO;
import org.example.service.transitional.ExpirationRule;
import org.example.service.transitional.IRule;
import org.example.service.transitional.ProductToBuy;
import org.example.service.transitional.QuantityRule;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


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
                .addMappings(mapper -> mapper.using(new AbstractConverter<List<CategoryDTO>, List<String>>() {
                    @Override
                    protected List<String> convert(@NonNull List<CategoryDTO> source) {
                        return source.stream().map(CategoryDTO::getName).toList();
                    }
                }).map(ProductToBuy::getCategories, ProductToBuyDTO::setCategories))
                .addMappings(new PropertyMap<>() {
                    @Override
                    protected void configure() {
                        map().setUuid(source.getProductDTO().getUuid());
                        map().setName(source.getProductDTO().getName());
                    }
                });

        return modelMapper;
    }

}
