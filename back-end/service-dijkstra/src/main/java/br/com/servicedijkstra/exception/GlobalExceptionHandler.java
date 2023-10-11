package br.com.servicedijkstra.exception;


import br.com.servicedijkstra.data.ApiErrorData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiErrorData> handle(ConstraintViolationException exception) {
        String errorMessage = new ArrayList<>(exception.getConstraintViolations()).get(0).getMessage();
        ApiErrorData apiError = new ApiErrorData(HttpStatus.BAD_REQUEST.value(), errorMessage,"detalhes");
            return new ResponseEntity<ApiErrorData>(apiError, null, HttpStatus.BAD_REQUEST);
    }

}
