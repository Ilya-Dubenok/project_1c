package org.example.enpoint.web.handler;

import com.google.common.base.CaseFormat;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.example.core.exception.GeneralException;
import org.example.core.exception.dto.GeneralExceptionDTO;
import org.example.core.exception.dto.StructuredExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Iterator;

@ControllerAdvice
public class ControllersExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = GeneralException.class)
    protected ResponseEntity<Object> handleGeneralException(GeneralException e, WebRequest request) {
        GeneralExceptionDTO exceptionDTO = new GeneralExceptionDTO(e.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {


        StructuredExceptionDTO structuredExceptionDTO = new StructuredExceptionDTO();

        fillStructuredExceptionDTOFromContraintViolationException(structuredExceptionDTO, e);

        return new ResponseEntity<>(structuredExceptionDTO, HttpStatus.BAD_REQUEST);
    }

    private void fillStructuredExceptionDTOFromContraintViolationException(StructuredExceptionDTO structuredExceptionDTO, ConstraintViolationException e) {
        Iterator<ConstraintViolation<?>> iterator = e.getConstraintViolations().iterator();

        while (iterator.hasNext()) {
            ConstraintViolation<?> constraintViolation = iterator.next();
            String propname = parseForPropNameInSnakeCase(constraintViolation);
            String message = constraintViolation.getMessage();
            structuredExceptionDTO.getPayload().put(propname, message);

        }

    }

    private String parseForPropNameInSnakeCase(ConstraintViolation<?> next) {

        Path propertyPath = next.getPropertyPath();

        Iterator<Path.Node> iterator = propertyPath.iterator();

        Path.Node node = null;

        while (iterator.hasNext()) {
            node = iterator.next();

        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, node.getName());


    }


}
