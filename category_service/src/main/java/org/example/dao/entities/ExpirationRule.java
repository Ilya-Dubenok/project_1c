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
@Table(name = "expiration_rule")
public class ExpirationRule implements IRule {

    @Id
    @Column(name= "id")
    private UUID id;

    private Integer daysTillExpiration;

    @Enumerated(EnumType.ORDINAL)
    private RuleType ruleType = RuleType.EXP;

    public ExpirationRule(UUID id, Integer daysTillExpiration) {
        this.id = id;
        this.daysTillExpiration = daysTillExpiration;
    }

    @Override
    public void apply() {

    }
}
