package org.example.service.transitional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.dto.product.ItemDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.rule.RuleType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuantityRule implements IRule {

    private RuleType ruleType = RuleType.QUANT;

    private Integer minimumQuantity;

    @Override
    public Integer getQuantityOfProductsToBuy(ProductDTO product) {
        List<ItemDTO> items = product.getItems();
        int sumOfItems = 0;
        if (null != items && items.size() > 0) {
            sumOfItems = items.stream()
                    .mapToInt(ItemDTO::getQuantity)
                    .sum();
        }
        int difference = minimumQuantity - sumOfItems;
        return Math.max(difference, 0);
    }

}
