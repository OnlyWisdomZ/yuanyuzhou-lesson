package com.ming.exception;

/** @author Ming */
public class RepeatRecordException extends RuntimeException {
    public RepeatRecordException(String coderMessage) {
        super(coderMessage);
    }
}