package org.example.enpoint.web;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.report.ProductToBuyDTO;
import org.example.core.dto.report.ReportDTO;
import org.example.service.api.IReportFileFormerService;
import org.example.service.api.IReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/report")
public class ReportController {

    private final IReportService reportService;

    private final IReportFileFormerService reportFileFormerService;

    @GetMapping("/short")
    public List<ProductToBuyDTO> getListOfProducts() {
        return reportService.getProductsToBuyDTO();
    }

    @PostMapping("/full")
    public ReportDTO formFullReport() {
        return reportService.formReport();
    }

    @GetMapping("/full/{uuid}")
    public ReportDTO getFullReport(@PathVariable UUID uuid) {
        return reportService.gerReport(uuid);
    }

    @GetMapping(value = "/xlsx/{uuid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getXLSXReportFile(@PathVariable UUID uuid) {
        byte[] reportFileByteArray = reportFileFormerService.formXLSXReport(uuid);
        return ResponseEntity.status(HttpStatus.OK).header("Content-Disposition", "attachment; filename=report.xlsx").body(reportFileByteArray);
    }
}
