package org.example.service.utils;

import lombok.Getter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.util.HashSet;
import java.util.Set;

@Getter
public class WrittenDataContainer {

    private final Set<Row> categoryRows = new HashSet<>();

    private final Set<Row> productRows = new HashSet<>();

    private final CellStyle categoryRowsStyle;

    private final CellStyle productRowsStyle;

    public WrittenDataContainer(CellStyle categoryRowsStyle, CellStyle productRowsStyle) {
        this.categoryRowsStyle = categoryRowsStyle;
        this.productRowsStyle = productRowsStyle;
    }
}
