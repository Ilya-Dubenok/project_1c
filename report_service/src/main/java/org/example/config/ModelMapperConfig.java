package org.example.config;

import org.example.core.dto.rule.ExpirationRuleDTO;
import org.example.core.dto.rule.QuantityRuleDTO;
import org.example.service.transitional.ExpirationRule;
import org.example.service.transitional.IRule;
import org.example.service.transitional.QuantityRule;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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

        return modelMapper;
    }

}
