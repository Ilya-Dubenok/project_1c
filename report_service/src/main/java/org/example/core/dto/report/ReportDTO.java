package org.example.core.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {

    private UUID id;

    private LocalDateTime formedOn;

    private List<ReportDataDTO> data;

}
