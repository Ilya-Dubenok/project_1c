package org.example.config;

import org.example.core.dto.rule.*;
import org.example.dao.entities.Category;
import org.example.dao.entities.ExpirationRule;
import org.example.dao.entities.IRule;
import org.example.dao.entities.QuantityRule;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper
                .createTypeMap(Category.class, UUID.class)
                .setCondition(context -> null != context.getSource())
                .setConverter(context -> context.getSource().getId());

        modelMapper
                .createTypeMap(QuantityRule.class, RuleCreateDTO.class)
                .setProvider(context -> modelMapper.map(context.getSource(), QuantityRuleDTO.class));

        modelMapper
                .createTypeMap(ExpirationRule.class, RuleCreateDTO.class)
                .setProvider(context -> modelMapper.map(context.getSource(), ExpirationRuleDTO.class));

        modelMapper
                .createTypeMap(ExpirationRuleCreateDTO.class, IRule.class)
                .setProvider(context -> {
                    ExpirationRuleCreateDTO source = (ExpirationRuleCreateDTO) context.getSource();
                    ExpirationRule expirationRule = new ExpirationRule();
                    expirationRule.setId(UUID.randomUUID());
                    expirationRule.setDaysTillExpiration(source.getDaysTillExpiration());
                    return expirationRule;
                });

        modelMapper
                .createTypeMap(QuantityRuleCreateDTO.class, IRule.class)
                .setProvider(context -> {
                    QuantityRuleCreateDTO source = (QuantityRuleCreateDTO) context.getSource();
                    QuantityRule quantityRule = new QuantityRule();
                    quantityRule.setId(UUID.randomUUID());
                    quantityRule.setMinimumQuantity(source.getMinimumQuantity());
                    return quantityRule;
                });

        return modelMapper;
    }


}
