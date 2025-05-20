package org.example.exception;

import org.example.model.Worker;

public class BadUpdateParam extends RuntimeException{
    public BadUpdateParam (String message) { super(message);}
}