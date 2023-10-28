package org.example.utils.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.core.exception.dto.InternalExceptionDTO;
import org.hibernate.exception.ConstraintViolationException;

import java.util.*;

@Data
@AllArgsConstructor
public class DataBaseExceptionParser {

    private Map<String, ConstraintMapper> constraintMapperMap;

    public boolean fillIfExceptionRecognized(Throwable cause, InternalExceptionDTO internalExceptionDTO) {

        if (cause instanceof ConstraintViolationException) {
            String constraintName = ((ConstraintViolationException) cause).getConstraintName();
            ConstraintMapper constraint = findProperConstraint(constraintName);

            if (constraint != null) {
                internalExceptionDTO.setMessage(constraint.getErrorMessage());
                return true;
            }
            return false;

        }  else {
            Throwable innerCause = cause.getCause();
            if (innerCause == null || innerCause == cause) {
                return false;
            }
            return fillIfExceptionRecognized(innerCause, internalExceptionDTO);
        }

    }

    private ConstraintMapper findProperConstraint(String constraintName) {

        if (constraintName == null) {
            return null;
        }

        return constraintMapperMap.get(constraintName);
    }

    public static class Builder {

        private final Map<String, ConstraintMapper> constraintMapperMap = new HashMap<>();

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder addConstraint(ConstraintMapper constraintMapper) {
            constraintMapperMap.put(constraintMapper.getConstraintName(), constraintMapper);
            return this;
        }

        public DataBaseExceptionParser build() {
            return new DataBaseExceptionParser(this.constraintMapperMap);
        }

    }

}
