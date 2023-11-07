package org.example.core.dto.rule;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.dao.entities.product.RuleType;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpirationRuleDTO extends RuleDTO {

    private RuleType ruleType = RuleType.EXP;

    @NotNull(message = "must not be null")
    @Min(value = 0, message = "must not be less than 0")
    private Integer daysTillExpiration;

}
