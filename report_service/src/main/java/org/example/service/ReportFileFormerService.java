package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.report.ReportDTO;
import org.example.service.api.IReportFileFormerService;
import org.example.service.api.IXLSXReportFileFormerService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportFileFormerService implements IReportFileFormerService {

    private final ReportService reportService;

    private final IXLSXReportFileFormerService xlsxReportFileFormerService;

    @Override
    public byte[] formXLSXReport(UUID uuid) {
        ReportDTO reportDTO = reportService.gerReport(uuid);
        return xlsxReportFileFormerService.getXLSXReportFile(reportDTO);
    }
}
