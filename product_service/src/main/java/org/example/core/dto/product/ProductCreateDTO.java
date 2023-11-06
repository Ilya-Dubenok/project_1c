package org.example.core.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.dto.rule.RuleDTO;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {

    @NotBlank(message = "must not be null or blank")
    private String name;

    @NotBlank(message = "must not be null or blank")
    private UUID categoryId;

    @Valid
    private List<RuleDTO> rules;

    @Valid
    private List<ItemDTO> items;

}
