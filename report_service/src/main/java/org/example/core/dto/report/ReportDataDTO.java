package org.example.core.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dao.entities.CategoryData;
import org.example.dao.entities.ProductData;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDataDTO {

    private CategoryData category;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ProductData> products;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ReportDataDTO> subcategories;

}
