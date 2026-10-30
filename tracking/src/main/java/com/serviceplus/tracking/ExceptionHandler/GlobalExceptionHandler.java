package com.serviceplus.tracking.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SPRuntimeError.class)
    public ResponseEntity<ErrorDetails> handler(SPRuntimeError ex) {

        ErrorDetails err = new ErrorDetails(
                ex.getMessage(),
                ex.getErrorCode(),
                ex.getErrorCode().value()
        );

        err.setData(ex.getData());

        return ResponseEntity
                .status(ex.getErrorCode())
                .body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> global(Exception ex) {

        ex.printStackTrace();

        ErrorDetails err = new ErrorDetails(
                "Something went wrong",
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(err);
    }
}

