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
    Long wrongCounter = 300L;
    Long correctCounter = 200L;

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
    void getMaxAndMinShouldReturnExceptionWhenCounterIsNotCorrect() {
        Exception exception = assertThrows(Exception.class, ()
                -> {
            currencyService.getMaxAndMin(wrongCounter, correctCurrency);
        });
        String expectedMessage = "Designated range + 300 is to high, max counter is 255";
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage,actualMessage);
    }
    @Test
    void getMaxAndMinShouldReturnExceptionWhenCurrencyIsNotCorrect() {
        //when
        Exception exception = assertThrows(Exception.class, ()
                -> {
            currencyService.getMaxAndMin(correctCounter, wrongCurrency);
        });
        String expectedMessage = "You type incorrectly currency value :"+" " + wrongCurrency.toUpperCase();
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage,actualMessage);
    }
    @Test
    void getMaxAndMinShouldReturnCorrectStatementWhenAllDataAreCorrect() {
        //when
        String maxAndMin = currencyService.getMaxAndMin(correctCounter, correctCurrency);
        String expectedMessage = "For currency : USD max value was : 5.0239 , min value was : 4.1557 from last : 200 days";

        //then
        assertEquals(maxAndMin,expectedMessage);
    }
    @Test
    void getMajorDifferenceShouldReturnExceptionWhenCurrencyIsNotCorrect() {
        //when
        Exception exception = assertThrows(Exception.class, ()
                -> {
            currencyService.getMajorDifference(correctCounter, wrongCurrency);
        });
        String expectedMessage = "You type incorrectly currency value :"+" " + wrongCurrency.toUpperCase();
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage,actualMessage);
    }
    @Test
    void getMajorDifferenceShouldReturnExceptionWhenCounterIsNotCorrect() {
        Exception exception = assertThrows(Exception.class, ()
                -> {
            currencyService.getMajorDifference(wrongCounter, correctCurrency);
        });
        String expectedMessage = "Designated range + 300 is to high, max counter is 255";
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage,actualMessage);
    }
    @Test
    void getMajorDifferenceShouldReturnCorrectStatementWhenAllDataAreCorrect() {
        //when
        String majorDifference = currencyService.getMajorDifference(correctCounter, correctCurrency);
        String expectedMessage = "Major difference :\"2022-10-11\"=0.1004 , minor difference :\"2023-04-25\"=0.0834 from last : 200 days";

        //then
        assertEquals(majorDifference,expectedMessage);
    }
}