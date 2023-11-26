package org.example.service.api;

import org.example.core.dto.report.ProductToBuyDTO;
import org.example.core.dto.report.ReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IReportService {

    List<ProductToBuyDTO> getProductsToBuyDTO();

    ReportDTO formReport();

    ReportDTO gerReport(UUID uuid);

    Page<ReportDTO> getPage(Pageable pageable);

    void deleteAllReports();
}
