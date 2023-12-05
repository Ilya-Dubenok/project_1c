package org.example.service.transitional;

import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.rule.RuleType;

public interface IRule {

    RuleType getRuleType();

    Integer getQuantityOfProductsToBuy(ProductDTO product);

}
