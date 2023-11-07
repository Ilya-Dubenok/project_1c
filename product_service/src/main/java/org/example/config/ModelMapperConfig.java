package org.example.config;

import org.example.core.dto.product.ItemDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.rule.ExpirationRuleDTO;
import org.example.core.dto.rule.QuantityRuleDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.dao.entities.product.ExpirationRule;
import org.example.dao.entities.product.IRule;
import org.example.dao.entities.product.Product;
import org.example.dao.entities.product.QuantityRule;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;


@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper
                .createTypeMap(ExpirationRule.class, RuleDTO.class)
                .setProvider(context -> modelMapper().map(context.getSource(), ExpirationRuleDTO.class));

        modelMapper
                .createTypeMap(QuantityRule.class, RuleDTO.class)
                .setProvider(context -> modelMapper().map(context.getSource(), QuantityRuleDTO.class));

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
                .createTypeMap(Product.class, ProductDTO.class)
                .setPostConverter(
                        context -> {
                            ProductDTO destination = context.getDestination();
                            List<ItemDTO> items = destination.getItems();
                            if (null != items && items.size() > 0){
                                items.sort(Comparator.comparing(ItemDTO::getExpiresAt, Comparator.nullsFirst(LocalDate::compareTo)));
                            }
                            return destination;
                        }
                );

        return modelMapper;
    }
}
