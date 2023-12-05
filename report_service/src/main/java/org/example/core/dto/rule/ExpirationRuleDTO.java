package org.example.core.dto.rule;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpirationRuleDTO extends RuleDTO {

    private RuleType ruleType = RuleType.EXP;

    private Integer daysTillExpiration;

}
