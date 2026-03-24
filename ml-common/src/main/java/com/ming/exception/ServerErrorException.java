package com.ming.exception;

/** @author Ming */
public class ServerErrorException extends RuntimeException {
    public ServerErrorException(String coderMessage) {
        super(coderMessage);
    }
}