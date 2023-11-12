package org.example.service.api;

import org.example.core.dto.report.PendingProductDTO;

import java.util.List;

public interface IReportService {

    List<PendingProductDTO> getListOfPendingProducts();

}
