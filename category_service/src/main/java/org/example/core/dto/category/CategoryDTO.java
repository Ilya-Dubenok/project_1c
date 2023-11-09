package org.example.core.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.dto.rule.RuleCreateDTO;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    private UUID uuid;

    private String name;

    private UUID parentUuid;

    private List<RuleCreateDTO> rules;


}
