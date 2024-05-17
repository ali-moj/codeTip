package com.jvpars.codetip.dto;

public class GenericPageableResponse<T> {

    public T result;
    public OperationResult operationResult;
    public String message;
    public Integer from;
    public Integer size;
    public String sort;
    public boolean reverse;
    public Long total;
    public Integer pageCount;
}
