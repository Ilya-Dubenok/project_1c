package org.example.core.dto.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StructuredExceptionDTO {

    private String logref = "structured_error";

    private Map<String, String> payload = new HashMap<>();



}
