package org.example.utils.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralExceptionDTO {

    private final String logref = "error";

    private String message;


}
