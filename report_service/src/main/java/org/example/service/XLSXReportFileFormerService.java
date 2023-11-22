package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.core.dto.report.ReportDTO;
import org.example.core.dto.report.ReportDataDTO;
import org.example.dao.entities.CategoryData;
import org.example.dao.entities.ProductData;
import org.example.service.api.IXLSXReportFileFormerService;
import org.example.service.utils.WrittenDataContainer;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class XLSXReportFileFormerService implements IXLSXReportFileFormerService {


    @Override
    public byte[] getXLSXReportFile(ReportDTO reportDTO) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Workbook workbook = formWorkbookForReport(reportDTO)) {
            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Workbook formWorkbookForReport(ReportDTO reportDTO) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("report");
        writeCoreReportData(sheet, reportDTO, 0, 0);
        addMetaInfoToReport(sheet, reportDTO, 3);
        return workbook;
    }

    private void writeCoreReportData(Sheet sheet, ReportDTO reportDTO, int startRow, int startColumn) {
        for (ReportDataDTO reportDataDTO : reportDTO.getData()) {
            WrittenDataContainer writtenDataContainer = createDataContainerForWorkbook(sheet);
            writeReportData(sheet, writtenDataContainer, reportDataDTO, startRow, startColumn);
            setStyleAndAlignColumns(sheet, writtenDataContainer, sheet.getActiveCell(), startRow);
            startRow = sheet.getActiveCell().getRow() + 2;
        }
        autoSizeAllColumns(sheet, startColumn, sheet.getActiveCell().getColumn());
    }

    private void writeReportData(Sheet sheet, WrittenDataContainer writtenDataContainer, ReportDataDTO reportDataDTO, int rowNum, int columnNum) {
        writeCategoryWithProductsAsBlock(sheet, writtenDataContainer, reportDataDTO.getCategory(), reportDataDTO.getProducts(), rowNum, columnNum);
        List<ReportDataDTO> innerDataList = reportDataDTO.getSubcategories();
        if (null != innerDataList && !innerDataList.isEmpty()) {
            columnNum++;
            for (ReportDataDTO innerData : innerDataList) {
                writeReportData(sheet, writtenDataContainer, innerData, sheet.getActiveCell().getRow() + 1, columnNum);
            }
        }
        sheet.groupRow(rowNum + 1, sheet.getActiveCell().getRow());
    }

    private void writeCategoryWithProductsAsBlock(Sheet sheet, WrittenDataContainer writtenDataContainer, CategoryData categoryData, List<ProductData> productDataList, int rowNum, int columnNum) {
        Row categoryRow = sheet.createRow(rowNum++);
        writtenDataContainer.getCategoryRows().add(categoryRow);
        createCellAndSetStringValue(categoryRow, columnNum, categoryData.getName());
        if (null != productDataList && !productDataList.isEmpty()) {
            for (ProductData productData : productDataList) {
                writeProductData(sheet, productData, rowNum++, columnNum);
                writtenDataContainer.getProductRows().add(sheet.getRow(sheet.getActiveCell().getRow()));
            }
        }
    }

    private CellAddress writeProductData(Sheet sheet, ProductData productData, int rowNum, int columnNum) {
        Row productRow = sheet.createRow(rowNum);
        createCellAndSetStringValue(productRow, columnNum, productData.getName());
        createCellAndSetIntegerValue(productRow, columnNum + 1, productData.getQuantity());
        return sheet.getActiveCell();
    }

    private void createCellAndSetStringValue(Row row, int column, String value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        row.getSheet().setActiveCell(new CellAddress(cell.getAddress()));
    }

    private void createCellAndSetIntegerValue(Row row, int column, Integer value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        row.getSheet().setActiveCell(new CellAddress(cell.getAddress()));
    }

    private void addMetaInfoToReport(Sheet sheet, ReportDTO reportDTO, int rightOffset) {
        int metaInfoStartColumn = sheet.getActiveCell().getColumn() + rightOffset;

        Row uuidRow = sheet.getRow(0);
        createCellAndSetStringValue(uuidRow, metaInfoStartColumn, "UUID: ");
        createCellAndSetStringValue(uuidRow, metaInfoStartColumn + 1, reportDTO.getUuid().toString());
        Row formedOnRow = sheet.getRow(1);
        createCellAndSetStringValue(formedOnRow, metaInfoStartColumn, "Formed on: ");
        createCellAndSetStringValue(formedOnRow, metaInfoStartColumn + 1, reportDTO.getFormedOn().toString());

        autoSizeAllColumns(sheet, metaInfoStartColumn, metaInfoStartColumn + 1);
    }

    private void setStyleAndAlignColumns(Sheet sheet, WrittenDataContainer writtenDataContainer, CellAddress lastActiveCell, int startRow) {
        int rightmostColumnNum = findRightmostColumnNum(startRow, lastActiveCell.getRow(), sheet);
        writtenDataContainer.getCategoryRows().forEach(row -> {
            setCategoryRowStyle(writtenDataContainer, row);
            mergeCellsForSingleRow(row, row.getFirstCellNum(), rightmostColumnNum);
        });
        writtenDataContainer.getProductRows().forEach(row -> {
            alignProductRowToRightColumn(row, rightmostColumnNum);
            setProductRowStyle(writtenDataContainer, row);
        });
    }

    private int findRightmostColumnNum(int startRow, int endRow, Sheet sheet) {
        int rightmostColumnNum = 0;
        for (int i = startRow; i <= endRow; i++) {
            Row currentRow = sheet.getRow(i);
            rightmostColumnNum = Math.max(rightmostColumnNum, currentRow.getLastCellNum() - 1);
        }
        return rightmostColumnNum;
    }

    private void setCategoryRowStyle(WrittenDataContainer writtenDataContainer, Row row) {
        CellStyle cellStyle = writtenDataContainer.getCategoryRowsStyle();
        Cell cell = row.getCell(row.getFirstCellNum());
        cell.setCellStyle(cellStyle);
    }

    private void setProductRowStyle(WrittenDataContainer writtenDataContainer, Row row) {
        CellStyle cellStyle = writtenDataContainer.getProductRowsStyle();
        Cell cell = row.getCell(row.getFirstCellNum());
        cell.setCellStyle(cellStyle);
    }

    private void alignProductRowToRightColumn(Row row, int rightmostColumnNum) {
        Cell quantityCell = row.getCell(row.getLastCellNum() - 1);
        if (quantityCell.getColumnIndex() < rightmostColumnNum) {
            moveSingleCellDataToTargetColumn(quantityCell, rightmostColumnNum);
            mergeCellsForSingleRow(row, row.getFirstCellNum(), rightmostColumnNum - 1);
        }
    }

    private void mergeCellsForSingleRow(Row row, int firstColumnNum, int lastColumnNum) {
        int rowNum = row.getRowNum();
        row.getSheet().addMergedRegion(new CellRangeAddress(rowNum, rowNum, firstColumnNum, lastColumnNum));
    }

    private void moveSingleCellDataToTargetColumn(Cell cell, int targetColumnNum) {
        Cell targetCell = cell.getRow().createCell(targetColumnNum);
        CellUtil.copyCell(cell, targetCell, new CellCopyPolicy(), null);
        cell.setBlank();
    }

    private void autoSizeAllColumns(Sheet sheet, int startColumn, int endColum) {
        for (int i = startColumn; i <= endColum; i++) {
            sheet.autoSizeColumn(i, true);
        }
    }

    private WrittenDataContainer createDataContainerForWorkbook(Sheet sheet) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle categoryRowStyle = formCategoryRowStyle(workbook);
        CellStyle productRowStyle = formProductRowStyle(workbook);
        return new WrittenDataContainer(categoryRowStyle, productRowStyle);
    }

    private CellStyle formCategoryRowStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    private CellStyle formProductRowStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 181, (byte) 198, (byte) 255}));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }
}
