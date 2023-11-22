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
            //TODO CHANGE HANDLING
            throw new RuntimeException(e);
        }
    }

    private Workbook formWorkbookForReport(ReportDTO reportDTO) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("report");
        List<ReportDataDTO> data = reportDTO.getData();
        fillReportWithAllData(sheet, data, 0, 0);
        return workbook;
    }

    private void fillReportWithAllData(Sheet sheet, List<ReportDataDTO> data, int startRow, int startColumn) {
        for (ReportDataDTO reportDataDTO : data) {
            WrittenDataContainer writtenDataContainer = createDataContainerForWorkbook(sheet);
            CellAddress lastActiveCell = writeReportData(sheet, writtenDataContainer, reportDataDTO, startRow, startColumn);
            setStyleAndAlignColumns(sheet, writtenDataContainer, lastActiveCell, startRow);
            startRow = lastActiveCell.getRow() + 2;
        }
    }

    private CellAddress writeReportData(Sheet sheet, WrittenDataContainer writtenDataContainer, ReportDataDTO reportDataDTO, int rowNum, int columnNum) {
        CellAddress lastActiveCell = writeCategoryWithProductsAsBlock(sheet, writtenDataContainer, reportDataDTO.getCategory(), reportDataDTO.getProducts(), rowNum, columnNum);
        List<ReportDataDTO> innerDataList = reportDataDTO.getSubcategories();
        if (null != innerDataList && !innerDataList.isEmpty()) {
            columnNum = columnNum + 1;
            for (ReportDataDTO innerData : innerDataList) {
                lastActiveCell = writeReportData(sheet, writtenDataContainer, innerData, lastActiveCell.getRow() + 1, columnNum);
            }
        }
        sheet.groupRow(rowNum + 1, lastActiveCell.getRow());
        return lastActiveCell;
    }

    private CellAddress writeCategoryWithProductsAsBlock(Sheet sheet, WrittenDataContainer writtenDataContainer, CategoryData categoryData, List<ProductData> productDataList, int rowNum, int columnNum) {
        Row categoryRow = sheet.createRow(rowNum++);
        writtenDataContainer.getCategoryRows().add(categoryRow);
        Cell cagegoryCell = categoryRow.createCell(columnNum);
        cagegoryCell.setCellValue(categoryData.getName());
        CellAddress cellAddress = new CellAddress(cagegoryCell);
        if (null != productDataList && !productDataList.isEmpty()) {
            for (ProductData productData : productDataList) {
                cellAddress = writeProductData(sheet, productData, rowNum++, columnNum);
                writtenDataContainer.getProductRows().add(sheet.getRow(cellAddress.getRow()));
            }
        }
        return cellAddress;
    }

    private CellAddress writeProductData(Sheet sheet, ProductData productData, int rowNum, int columnNum) {
        Row productRow = sheet.createRow(rowNum);
        productRow.createCell(columnNum).setCellValue(productData.getName());
        Cell productQuantityCell = productRow.createCell(columnNum + 1);
        productQuantityCell.setCellValue(productData.getQuantity());
        return productQuantityCell.getAddress();
    }

    private void setStyleAndAlignColumns(Sheet sheet, WrittenDataContainer writtenDataContainer, CellAddress lastActiveCell, int startRow) {
        int rightmostColumnNum = findRightmostColumnNum(startRow, lastActiveCell.getRow(), sheet);
        writtenDataContainer.getCategoryRows().forEach(row -> {
            setCategoryRowStyle(writtenDataContainer, row);
            mergeCellsForSingleRow(row, row.getFirstCellNum(), rightmostColumnNum);
        });

        //TODO change logic to handle product cells moving

        writtenDataContainer.getProductRows().forEach(row -> {
            alignProductRowToRightColumn(sheet, row, rightmostColumnNum);
            setProductRowStyle(writtenDataContainer, row);
        });

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

    private int findRightmostColumnNum(int startRow, int endRow, Sheet sheet) {
        int rightmostColumnNum = 0;
        for (int i = startRow; i <= endRow; i++) {
            Row currentRow = sheet.getRow(i);
            rightmostColumnNum = Math.max(rightmostColumnNum, currentRow.getLastCellNum() - 1);
        }
        return rightmostColumnNum;
    }

    private void alignProductRowToRightColumn(Sheet sheet, Row row, int rightmostColumnNum) {
        Cell quantityCell = row.getCell(row.getLastCellNum()-1);
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
