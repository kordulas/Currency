package com.example.Currency.exception;

public class WrongDataException extends RuntimeException{
    public WrongDataException(String message){
        super(message);
    }
}
