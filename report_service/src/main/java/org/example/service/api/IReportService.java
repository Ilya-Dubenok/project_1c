package org.example.service.api;

import org.example.core.dto.report.ProductToBuyDTO;

import java.util.List;

public interface IReportService {

    List<ProductToBuyDTO> getProductsToBuy();

}
