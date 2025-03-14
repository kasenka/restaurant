package org.example.exception;

public class BadRegisterParam extends RuntimeException{
    public BadRegisterParam (String message) { super(message);}
}