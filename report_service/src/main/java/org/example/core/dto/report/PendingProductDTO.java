package org.example.core.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingProductDTO {

    private String category;

    private String name;

    private Integer quantity;

}
