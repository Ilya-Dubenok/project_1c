package org.example.service.transitional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.rule.RuleType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpirationRule implements IRule {

    private Integer daysTillExpiration;

    private RuleType ruleType = RuleType.EXP;

    @Override
    public Integer getQuantityOfProductsToBuy(ProductDTO product) {
        return 0;
    }

}
