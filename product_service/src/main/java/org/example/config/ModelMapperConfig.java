package org.example.config;

import org.example.core.dto.rule.ExpirationRuleDTO;
import org.example.core.dto.rule.QuantityRuleDTO;
import org.example.core.dto.rule.RuleDTO;
import org.example.dao.entities.product.ExpirationRule;
import org.example.dao.entities.product.QuantityRule;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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

        return modelMapper;
    }
}
