package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.core.dto.report.ReportDTO;
import org.example.core.dto.report.ReportDataDTO;
import org.example.dao.entities.CategoryData;
import org.example.dao.entities.ProductData;
import org.example.service.api.IXLSXReportFileFormerService;
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
        fillReportWithAllData(workbook, sheet, data, 0, 0);
        return workbook;

    }

    private void fillReportWithAllData(Workbook workbook, Sheet sheet, List<ReportDataDTO> data, int rowNum, int columnNum) {
        for (ReportDataDTO reportDataDTO : data) {
            CellAddress lastActiveCell = writeReportData(workbook, sheet, reportDataDTO, rowNum, columnNum);
            rowNum = lastActiveCell.getRow() + 2;
        }
    }

    private CellAddress writeReportData(Workbook workbook, Sheet sheet, ReportDataDTO reportDataDTO, int rowNum, int columnNum) {
        CellAddress lastActiveCell = writeCategoryWithProductsAsBlock(workbook, sheet, reportDataDTO.getCategory(), reportDataDTO.getProducts(), rowNum, columnNum);
        List<ReportDataDTO> innerDataList = reportDataDTO.getSubcategories();
        if (null != innerDataList && !innerDataList.isEmpty()) {
            columnNum = columnNum + 1;
            for (ReportDataDTO innerData : innerDataList) {
                lastActiveCell = writeReportData(workbook, sheet, innerData, lastActiveCell.getRow() + 1, columnNum);
            }
        }
        return lastActiveCell;
    }

    private CellAddress writeCategoryWithProductsAsBlock(Workbook workbook, Sheet sheet, CategoryData categoryData, List<ProductData> productDataList, int rowNum, int columnNum) {
        Cell categoryNameRowCell = sheet.createRow(rowNum++).createCell(columnNum);
        categoryNameRowCell.setCellValue(categoryData.getName());
        CellAddress cellAddress = new CellAddress(categoryNameRowCell);

        if (null != productDataList && !productDataList.isEmpty()) {
            for (ProductData productData : productDataList) {
                cellAddress = writeProductData(workbook, sheet, productData, rowNum++, columnNum);
            }
        }
        return cellAddress;
    }

    private CellAddress writeProductData(Workbook workbook, Sheet sheet, ProductData productData, int rowNum, int columnNum) {
        Row productRow = sheet.createRow(rowNum);
        productRow.createCell(columnNum).setCellValue(productData.getName());
        Cell productQuantityCell = productRow.createCell(columnNum + 1);
        productQuantityCell.setCellValue(productData.getQuantity());
        return productQuantityCell.getAddress();
    }

}
