package org.example.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.IRule;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDTO {

    @NotBlank
    private String name;

    private UUID parentUuid;

    private List<IRule> rules;

}
