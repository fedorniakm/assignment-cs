package com.fedorniakm.demo.controller;

import com.fedorniakm.demo.model.Errors;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class,})
    public ResponseEntity<Errors> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var errors = new Errors();
        e.getBindingResult()
                .getGlobalErrors()
                .forEach(error -> errors.addError(
                        HttpStatus.BAD_REQUEST.value(),
                        error.getDefaultMessage(),
                        ""
                ));
        e.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.addError(
                        HttpStatus.BAD_REQUEST.value(),
                        "Field [" + error.getField() + "] is not valid.",
                        error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,})
    public ResponseEntity<Errors> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        var errors = new Errors();
        errors.addError(HttpStatus.BAD_REQUEST.value(),
                "Param [" + e.getName() + "] is not valid",
                "Param is expected to be " + e.getRequiredType().getSimpleName());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({DateTimeParseException.class})
    public ResponseEntity<Errors> handleHttpMessageNotReadableException(DateTimeParseException e) {
        var errors = new Errors();
        errors.addError(HttpStatus.BAD_REQUEST.value(),
                "Input date [" + e.getParsedString() + "] is not valid or has a wrong format.",
                e.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<Errors> handleNullPointerException(NullPointerException e) {
        var errors = new Errors();
        errors.addError(400,
                e.getMessage(),
                e.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

}
