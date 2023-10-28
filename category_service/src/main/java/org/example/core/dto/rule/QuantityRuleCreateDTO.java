package org.example.core.dto.rule;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityRuleCreateDTO extends RuleCreateDTO {

    @NotNull(message = "must not be null")
    @Min(value = 0, message = "must not be less than 0")
    @JsonProperty("minimum_quantity")
    private Integer minimumQuantity;

}
