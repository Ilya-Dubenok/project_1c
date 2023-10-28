package org.example.core.dto.rule;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpirationRuleCreateDTO extends RuleCreateDTO {

    @NotNull(message = "must not be null")
    @Min(value = 0, message = "must not be less than 0")
    private Integer daysTillExpiration;



}
