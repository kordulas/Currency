package com.example.Currency.exception;

public class WrongCurrencyName extends RuntimeException {
    public WrongCurrencyName(String currency) {
        super(currency);
    }

    public WrongCurrencyName(String message, String name) {
        this(message + " " + name);
    }
}