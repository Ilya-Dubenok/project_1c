package org.example.service.api;

import org.example.core.dto.report.ProductToBuyDTO;
import org.example.dao.entities.Report;

import java.util.List;

public interface IReportService {

    List<ProductToBuyDTO> getProductsToBuyDTO();

    Report formReport();
}
