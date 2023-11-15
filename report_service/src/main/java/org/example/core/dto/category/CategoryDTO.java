package org.example.core.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.dto.rule.RuleDTO;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private UUID uuid;

    private String name;

    private UUID parentUuid;

    private List<RuleDTO> rules;

}
