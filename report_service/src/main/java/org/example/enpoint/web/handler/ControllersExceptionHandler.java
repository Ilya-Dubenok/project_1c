package org.example.enpoint.web.handler;

import lombok.RequiredArgsConstructor;
import org.example.core.dto.exception.EntityNotFoundException;
import org.example.core.dto.exception.InternalException;
import org.example.core.dto.exception.OtherServiceUnavailableException;
import org.example.core.dto.exception.RequestNotFromGatewayException;
import org.example.core.dto.exception.dto.InternalExceptionDTO;
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


@ControllerAdvice
@RequiredArgsConstructor
public class ControllersExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String DEFAULT_INTERNAL_ERROR_MESSAGE = "Some error occurred during operation";

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleGeneralException(Exception e, WebRequest request) {
        return new ResponseEntity<>(DEFAULT_INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = InternalException.class)
    protected ResponseEntity<Object> handleInternalException(InternalException e, WebRequest request) {
        InternalExceptionDTO exceptionDTO = new InternalExceptionDTO(e.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e, WebRequest request) {
        return new ResponseEntity<>(new InternalExceptionDTO(DEFAULT_INTERNAL_ERROR_MESSAGE), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e, WebRequest request) {
        InternalExceptionDTO internalExceptionDTO = new InternalExceptionDTO(e.getMessage());
        return new ResponseEntity<>(internalExceptionDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RequestNotFromGatewayException.class)
    public ResponseEntity<Object> handleRequestNotFromGatewayException(RequestNotFromGatewayException e, WebRequest request) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = OtherServiceUnavailableException.class)
    protected ResponseEntity<Object> handleOtherServiceUnavailableException(OtherServiceUnavailableException e, WebRequest request) {
        return new ResponseEntity<>(new InternalExceptionDTO("Unfortunately, the request could not be processed right now. Please, try later"), HttpStatus.BAD_REQUEST);
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
