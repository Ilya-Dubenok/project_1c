package org.example.service.api;

import org.example.core.dto.report.ReportDTO;

public interface IXLSXReportFileFormerService {

    byte[] getXLSXReportFile(ReportDTO reportDTO);

}
