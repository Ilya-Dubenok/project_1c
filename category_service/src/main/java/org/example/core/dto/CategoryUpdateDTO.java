package org.example.core.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.dto.rule.RuleCreateDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDTO {

    @NotBlank(message = "name must not be null o blank")
    private String name;

    @Valid
    private List<RuleCreateDTO> rules;

}
