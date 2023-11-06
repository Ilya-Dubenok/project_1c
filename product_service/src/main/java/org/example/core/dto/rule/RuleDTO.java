package org.example.core.dto.rule;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = QuantityRuleDTO.class, name = "QUANT"),
        @JsonSubTypes.Type(value = ExpirationRuleDTO.class, name = "EXP")
})
@Schema(discriminatorProperty = "type",
discriminatorMapping = {
        @DiscriminatorMapping(value = "QUANT",schema = QuantityRuleDTO.class),
        @DiscriminatorMapping(value = "EXP",schema = ExpirationRuleDTO.class)
})
@NoArgsConstructor
public abstract class RuleDTO {


}
