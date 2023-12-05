package org.example.dao.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportData {

    private CategoryData category;

    private List<ProductData> products;

    @Field(name = "subcategories")
    private List<ReportData> innerDataList;

}
