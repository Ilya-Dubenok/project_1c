package org.example.core.dto.rule;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpirationRuleDTO extends RuleDTO {

    @NotNull(message = "must not be null")
    @Min(value = 0, message = "must not be less than 0")
    private Integer daysTillExpiration;

}
