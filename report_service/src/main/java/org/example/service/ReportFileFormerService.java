package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.report.ReportDTO;
import org.example.service.api.IReportFileFormerService;
import org.example.service.api.IReportService;
import org.example.service.api.IXLSXReportFileFormerService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportFileFormerService implements IReportFileFormerService {

    private final IReportService reportService;

    private final IXLSXReportFileFormerService xlsxReportFileFormerService;

    @Override
    public byte[] formXLSXReport(UUID id) {
        ReportDTO reportDTO = reportService.gerReport(id);
        return xlsxReportFileFormerService.getXLSXReportFile(reportDTO);
    }
}
