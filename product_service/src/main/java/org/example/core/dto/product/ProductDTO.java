package org.example.core.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.dto.rule.RuleDTO;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private UUID uuid;

    private UUID categoryId;

    private String name;

    private List<RuleDTO> rules;

    private List<ItemDTO> items;

}
