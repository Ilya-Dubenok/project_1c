package org.example.dao.entities.product;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpirationRule implements IRule {

    private Integer daysTillExpiration;

    private RuleType ruleType = RuleType.EXP;

    public ExpirationRule(Integer daysTillExpiration) {
        this.daysTillExpiration = daysTillExpiration;
    }

    @Override
    public void apply() {

    }
}
