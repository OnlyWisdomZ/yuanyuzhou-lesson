package com.ming.exception;

/** @author Ming */
public class IllegalParamException extends RuntimeException {
    public IllegalParamException(String coderMessage) {
        super(coderMessage);
    }
}