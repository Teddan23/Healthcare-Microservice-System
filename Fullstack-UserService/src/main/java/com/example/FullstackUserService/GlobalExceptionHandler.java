package com.example.FullstackUserService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Hantera HttpMessageNotReadableException (t.ex. när JSON-formatet är felaktigt)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleInvalidRequest(HttpMessageNotReadableException e) {
        return "Invalid input: " + e.getMessage();
    }

    // Hantera generella undantag som inte är specifika för JSON
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleGenericException(Exception e) {
        return "An unexpected error occurred: " + e.getMessage();
    }
}