package com.aditya.f1hub.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String resourceName,
                                          String fieldName,
                                          Object fieldValue) {

        super(String.format(
                "%s already exists with %s : '%s'",
                resourceName,
                fieldName,
                fieldValue
        ));
    }

}