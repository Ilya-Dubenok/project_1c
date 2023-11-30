package org.example.service.api;

import org.example.core.dto.report.ProductToBuyDTO;
import org.example.core.dto.report.ReportDTO;

import java.util.List;
import java.util.UUID;

public interface IReportService {

    List<ProductToBuyDTO> getProductsToBuyDTO();

    ReportDTO formReport();

    ReportDTO gerReport(UUID id);
}
