package org.example.dto.rule;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.RuleType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpirationRuleDTO extends ExpirationRuleCreateDTO {



    private UUID uuid;

    @JsonProperty("rule_type")
    private RuleType ruleType = RuleType.EXP;


}
