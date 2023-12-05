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
    @Column(name= "id")
    private UUID id;

    private Integer minimumQuantity;

    @Enumerated(EnumType.ORDINAL)
    private RuleType ruleType = RuleType.QUANT;

    public QuantityRule(UUID id, Integer minimumQuantity) {
        this.id = id;
        this.minimumQuantity = minimumQuantity;
    }

    @Override
    public void apply() {

    }
}
