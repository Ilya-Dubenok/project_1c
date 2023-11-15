package org.example.core.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductToBuyDTO {

    private UUID uuid;

    private String name;

    private List<String> categories;

    private Integer quantity;

}
