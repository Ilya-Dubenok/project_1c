package org.example.endpoint.web.handler;

import lombok.RequiredArgsConstructor;
import org.example.core.exception.InternalException;
import org.example.core.exception.ProductNotFoundException;
import org.example.core.exception.RequestNotFromGatewayException;
import org.example.core.exception.dto.InternalExceptionDTO;
import org.example.core.exception.dto.StructuredExceptionDTO;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllersExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = InternalException.class)
    protected ResponseEntity<Object> handleInternalException(InternalException e, WebRequest request) {
        InternalExceptionDTO exceptionDTO = new InternalExceptionDTO(e.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    protected ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException e, WebRequest request) {
        InternalExceptionDTO exceptionDTO = new InternalExceptionDTO("specified name is not unique");
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e, WebRequest request) {
        return new ResponseEntity<>(new InternalExceptionDTO("some error occurred during operation"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = ProductNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException e, WebRequest request) {
        InternalExceptionDTO internalExceptionDTO = new InternalExceptionDTO(e.getMessage());
        return new ResponseEntity<>(internalExceptionDTO,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RequestNotFromGatewayException.class)
    public ResponseEntity<Object> handleRequestNotFromGatewayException(RequestNotFromGatewayException e, WebRequest request) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        if (fieldErrors.size() > 0) {
            Map<String, String> fieldAndDescripionMap = new HashMap<>();
            for (FieldError error : fieldErrors) {
                fieldAndDescripionMap.put(error.getField(), error.getDefaultMessage());
            }
            StructuredExceptionDTO structuredExceptionDTO = new StructuredExceptionDTO();
            structuredExceptionDTO.setPayload(fieldAndDescripionMap);
            return new ResponseEntity<>(structuredExceptionDTO, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(new InternalExceptionDTO("invalid input data"), HttpStatus.BAD_REQUEST);
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


}
