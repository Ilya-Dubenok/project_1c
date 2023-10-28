package org.example.config;

import org.example.dao.entities.Category;
import org.example.core.dto.rule.ExpirationRuleDTO;
import org.example.core.dto.rule.RuleCreateDTO;
import org.example.core.dto.rule.QuantityRuleDTO;
import org.example.dao.entities.ExpirationRule;
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
                .setConverter(context -> context.getSource().getUuid());

        modelMapper
                .createTypeMap(QuantityRule.class, RuleCreateDTO.class)
                .setProvider(context -> modelMapper.map(context.getSource(), QuantityRuleDTO.class));

        modelMapper
                .createTypeMap(ExpirationRule.class, RuleCreateDTO.class)
                .setProvider(context -> modelMapper.map(context.getSource(), ExpirationRuleDTO.class));


        return modelMapper;
    }


}
