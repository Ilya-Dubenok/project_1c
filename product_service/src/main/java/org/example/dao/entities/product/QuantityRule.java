package org.example.dao.entities.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuantityRule implements IRule{

    private Integer minimumQuantity;

    private RuleType ruleType = RuleType.QUANT;

    public QuantityRule(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    @Override
    public void apply() {

    }
}
