package com.jvpars.codetip.utils;


import com.jvpars.codetip.dto.GenericPageableResponse;
import com.jvpars.codetip.dto.GenericResponse;
import com.jvpars.codetip.dto.OperationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GenericResponseGenerator {

    public static <T> ResponseEntity<GenericResponse<T>> success() {
        GenericResponse<T> response = new GenericResponse<>();
        response.operationResult = OperationResult.Success;
        response.message = null;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<GenericResponse<T>> success(T value) {
        GenericResponse<T> response = new GenericResponse<>();
        response.operationResult = OperationResult.Success;
        response.message = null;
        response.result = value;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<GenericResponse<T>> error(String message) {
        GenericResponse<T> response = new GenericResponse<>();
        response.operationResult = OperationResult.ServerError;
        response.message = message;
        response.result = null;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<GenericResponse<T>> error() {
        GenericResponse<T> response = new GenericResponse<>();
        response.operationResult = OperationResult.ServerError;
        response.message = null;
        response.result = null;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<GenericResponse<T>> response(OperationResult result) {
        GenericResponse<T> response = new GenericResponse<>();
        response.operationResult = result;
        response.message = null;
        response.result = null;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<GenericResponse<T>> response(OperationResult status , String message) {
        GenericResponse<T> response = new GenericResponse<>();
        response.result = null;
        response.operationResult = status;
        response.message = message;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public static <T> ResponseEntity<GenericPageableResponse<T>> pageable(Long total , Integer pageCount , T result ){

        GenericPageableResponse<T> response = new GenericPageableResponse<>();
        response.total = total;
        response.pageCount = pageCount;
        response.result = result;
        response.operationResult = OperationResult.Success;
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    public static <T> ResponseEntity pageableError(String message){

        GenericPageableResponse<T> response = new GenericPageableResponse<>();
        response.message = message;
        response.operationResult = OperationResult.ServerError;
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
