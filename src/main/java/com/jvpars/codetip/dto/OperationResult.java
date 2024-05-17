package com.jvpars.codetip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum OperationResult {
    Success, Fail , NotFound , Duplicate , ServerError
}
