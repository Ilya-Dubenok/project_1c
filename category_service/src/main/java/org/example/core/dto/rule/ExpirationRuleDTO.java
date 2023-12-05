package org.example.core.dto.rule;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dao.entities.RuleType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpirationRuleDTO extends ExpirationRuleCreateDTO {

    private UUID id;

    private RuleType ruleType = RuleType.EXP;


}
