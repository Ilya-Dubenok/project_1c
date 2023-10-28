package org.example.enpoint.web.handler;

import com.google.common.base.CaseFormat;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import org.example.core.exception.InternalException;
import org.example.utils.exception.DataBaseExceptionParser;
import org.example.core.exception.dto.InternalExceptionDTO;
import org.example.core.exception.dto.StructuredExceptionDTO;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Iterator;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllersExceptionHandler extends ResponseEntityExceptionHandler {

    private final DataBaseExceptionParser dataBaseExceptionParser;

    @ExceptionHandler(value = InternalException.class)
    protected ResponseEntity<Object> handleGeneralException(InternalException e, WebRequest request) {
        InternalExceptionDTO exceptionDTO = new InternalExceptionDTO(e.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {

        StructuredExceptionDTO structuredExceptionDTO = new StructuredExceptionDTO();

        fillStructuredExceptionDTOFromContraintViolationException(structuredExceptionDTO, e);

        return new ResponseEntity<>(structuredExceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e, WebRequest request) {

        InternalExceptionDTO generalExceptionToFill = new InternalExceptionDTO();
        if (dataBaseExceptionParser.fillIfExceptionRecognized(e, generalExceptionToFill)) {
            return new ResponseEntity<>(generalExceptionToFill, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("some error occurred during operation", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String propertyName = ex.getPropertyName();

        String message = propertyName + " is malformed";

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>("HTTP message is malformed", HttpStatus.BAD_REQUEST);
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
