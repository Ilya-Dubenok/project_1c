package org.example.dao.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "quantity_rule")
public class QuantityRule implements IRule{

    @Id
    private UUID uuid;

    private Integer minimumQuantity;

    @Enumerated(EnumType.STRING)
    private RuleType ruleType = RuleType.QUANT;

    public QuantityRule(UUID uuid, Integer minimumQuantity) {
        this.uuid = uuid;
        this.minimumQuantity = minimumQuantity;
    }

    @Override
    public RuleType getRuleType() {
        return this.ruleType;
    }

    @Override
    public void apply() {

    }
}
