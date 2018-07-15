package br.com.cedran.city.gateway.web.mvc;

import br.com.cedran.city.gateway.web.mvc.dto.ErrorResponse;
import br.com.cedran.city.model.ErrorCode;
import br.com.cedran.city.usecase.exceptions.BusinessException;
import br.com.cedran.city.usecase.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GenericControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessException(final BusinessException businessException) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(businessException.getErrorCode());
        errorResponse.setParams(businessException.getParams());
        errorResponse.setErrorMessage(businessException.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unexpectedException(final Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCode.UNEXPECTED_ERROR.getErrorCode());
        errorResponse.setErrorMessage(ErrorCode.UNEXPECTED_ERROR.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> argumentMismatch(final MethodArgumentTypeMismatchException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCode.INVALID_ARGUMENT.getErrorCode());
        errorResponse.setErrorMessage(ErrorCode.INVALID_ARGUMENT.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> argumentMismatch(final MethodArgumentNotValidException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCode.INVALID_ARGUMENT.getErrorCode());
        errorResponse.setErrorMessage(ErrorCode.INVALID_ARGUMENT.getMessage());
        errorResponse.setParams(exception.getBindingResult().getAllErrors().stream().map(error -> ((FieldError) error)).map(error -> String.format("%s %s", error.getField(), error.getDefaultMessage())).collect(Collectors.toList()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public void notFound(final NotFoundException exception) {

    }
}
