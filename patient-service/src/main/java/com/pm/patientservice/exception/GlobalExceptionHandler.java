package com.pm.patientservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String , String>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>() ;
        ex.getBindingResult().getFieldErrors().forEach(error ->{
            errors.put(error.getField() , error.getDefaultMessage()) ;
        });
        return ResponseEntity.badRequest().body(errors);
    }


    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistException(EmailAlreadyExistException ex) {
        log.warn("Email already exists: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String , String>> handlePatientNotFoundException(PatientNotFoundException ex){
        log.warn("Patient not found :: {}" , ex.getMessage());
        Map<String , String> error = new  HashMap<>() ;
        error.put("message" , "Patient not found with the given id") ;
        return ResponseEntity.status(404).body(error);
    }

}
