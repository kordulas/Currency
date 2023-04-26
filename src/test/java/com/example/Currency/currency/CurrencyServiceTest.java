package com.example.Currency.currency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyServiceTest {

    private CurrencyService currencyService = new CurrencyService();
    String wrongCurrency = "us";
    String correctCurrency = "usd";
    String wrongDate = "2002-01-1";
    String correctDate = "2023-04-26";
    String wrongCounter = "300";
    String correctCounter = "200";

    @Test
    void shouldReturnExceptionWhenCurrencyIsNotCorrect() {
        //when
        Exception exception = assertThrows(Exception.class, ()
                -> {
            currencyService.getAvgPrice(correctDate, wrongCurrency);
        });
        String expectedMessage = "You type incorrectly currency value :"+" " + wrongCurrency.toUpperCase();
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage,actualMessage);
    }
    @Test
    void shouldReturnExceptionWhenDateIsNotCorrect() {
        //when
        Exception exception = assertThrows(Exception.class, ()
                -> {
            currencyService.getAvgPrice(wrongDate, correctCurrency);
        });
        String expectedMessage = "You type incorrect data format, use correctly pattern : yyyy-MM-dd";
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void getMaxAndMin() {
    }

    @Test
    void getMajorDifference() {
    }
}