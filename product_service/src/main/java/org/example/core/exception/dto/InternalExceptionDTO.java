package org.example.core.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalExceptionDTO {

    private final String logref = "error";

    private String message;


}
