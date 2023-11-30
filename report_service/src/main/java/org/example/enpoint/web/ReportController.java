package org.example.enpoint.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.core.dto.exception.dto.InternalExceptionDTO;
import org.example.core.dto.report.ProductToBuyDTO;
import org.example.core.dto.report.ReportDTO;
import org.example.service.api.IReportFileFormerService;
import org.example.service.api.IReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Report")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/report")
public class ReportController {

    private final IReportService reportService;

    private final IReportFileFormerService reportFileFormerService;

    @Operation(summary = "Get list of products to buy without full mapping to categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products to buy returned")})
    @GetMapping("/short")
    public List<ProductToBuyDTO> getListOfProducts() {
        return reportService.getProductsToBuyDTO();
    }

    @Operation(summary = "Place a request to form a full report and get the result")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ReportDTO successfully formed and returned")
    })
    @PostMapping("/full")
    public ReportDTO formFullReport() {
        return reportService.formReport();
    }

    @Operation(summary = "Get full report by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ReportDTO returned"),
            @ApiResponse(responseCode = "404", description = "No report found for this id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = InternalExceptionDTO.class))
            })
    })
    @GetMapping("/full/{id}")
    public ReportDTO getFullReport(@PathVariable UUID id) {
        return reportService.gerReport(id);
    }

    @Operation(summary = "Get full report formed in xlsx format by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ReportDTO returned"),
            @ApiResponse(responseCode = "404", description = "No report found for this id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = InternalExceptionDTO.class))
            })
    })
    @GetMapping(value = "/xlsx/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getXLSXReportFile(@PathVariable UUID id) {
        byte[] reportFileByteArray = reportFileFormerService.formXLSXReport(id);
        return ResponseEntity.status(HttpStatus.OK).header("Content-Disposition", "attachment; filename=report.xlsx").body(reportFileByteArray);
    }
}
