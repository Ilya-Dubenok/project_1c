package org.example.service.transitional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.dto.product.ItemDTO;
import org.example.core.dto.product.ProductDTO;
import org.example.core.dto.rule.RuleType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpirationRule implements IRule {

    private Integer daysTillExpiration;

    private RuleType ruleType = RuleType.EXP;

    @Override
    public Integer getQuantityOfProductsToBuy(ProductDTO product) {
        List<ItemDTO> items = product.getItems();
        if (null == daysTillExpiration || null == items || items.size() == 0) {
            return 0;
        }
        return items.stream()
                .filter(item -> null != item.getExpiresAt())
                .filter(item -> {
                    LocalDate notificationDate = item.getExpiresAt().minus(daysTillExpiration, ChronoUnit.DAYS);
                    return !notificationDate.isAfter(LocalDate.now());
                })
                .mapToInt(ItemDTO::getQuantity)
                .sum();
    }

}
