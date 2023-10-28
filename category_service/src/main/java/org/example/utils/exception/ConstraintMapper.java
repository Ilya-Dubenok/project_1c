package org.example.utils.exception;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConstraintMapper {

    private String constraintName;

    private String errorMessage;

}
