package com.example.Currency.currency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyServiceTest {

    private CurrencyService currencyService = new CurrencyService();
    String wrongCurrency = "us";
    String correctCurrency = "usd";
    String wrongDate = "2002-01-1";
    String weekendDay = "2023-04-22";
    String correctDate = "2023-04-25";
    String wrongCounter = "300";
    String correctCounter = "200";

    @Test
    void getAvgPriceShouldReturnExceptionWhenCurrencyIsNotCorrect() {
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
    void getAvgPriceShouldReturnExceptionWhenDateIsNotCorrect() {
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
    void getAvgPriceShouldReturnExceptionWhenDateIsWeekendDay() {
        //when
        Exception exception = assertThrows(Exception.class, ()
                -> {
            currencyService.getAvgPrice(weekendDay, correctCurrency);
        });
        String expectedMessage = "Select a day of the week other than the weekend";
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage,actualMessage);
    }
    @Test
    void getAvgPriceShouldReturnPreparedStatementIFValuesAreCorrect() {
        //when
        String avgPrice = currencyService.getAvgPrice(correctDate, correctCurrency);
        String expectedMessage = "For chosen currency :" + correctCurrency.toUpperCase() + ", on day :" + correctDate + ", average exchange rate was :4.1649";
        //then
        assertEquals(avgPrice,expectedMessage);
    }

    @Test
    void getMaxAndMin() {
    }

    @Test
    void getMajorDifference() {
    }
}