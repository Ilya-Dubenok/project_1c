package org.example.dto.rule;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type",visible = false)
@JsonSubTypes({
        @JsonSubTypes.Type(value = QuantityRuleCreateDTO.class, name = "QUANT"),
        @JsonSubTypes.Type(value = ExpirationRuleCreateDTO.class, name = "EXP")
})
@Schema(discriminatorProperty = "type",
discriminatorMapping = {
        @DiscriminatorMapping(value = "QUANT",schema = QuantityRuleCreateDTO.class),
        @DiscriminatorMapping(value = "EXP",schema = ExpirationRuleCreateDTO.class)
})
@NoArgsConstructor
public abstract class RuleCreateDTO {


}
