package org.example.core.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.dto.rule.RuleCreateDTO;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDTO {

    @NotBlank(message = "name must not be null o blank")
    private String name;

    @JsonProperty("parent_uuid")
    private UUID parentUuid;

    @Valid
    private List<RuleCreateDTO> rules;

}
