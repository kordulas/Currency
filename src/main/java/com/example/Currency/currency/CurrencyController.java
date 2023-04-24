package com.example.Currency.currency;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class CurrencyController {

    private CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @ApiOperation(value = "Provide rate",notes = "Provide average exchange rate")
    @RequestMapping(value = "/avgrate", params = {"date","currency"}, method = RequestMethod.GET)
    public String getAveragePrice(@RequestParam String date, @RequestParam String currency){
        return currencyService.getAvgPrice(date,currency);
    }

    @ApiOperation(value = "Provide max and min value",notes = "Provide max and min value in the time range given by user")
    @RequestMapping(value = "maxminrate",params = {"daysCounter","currency"},method = RequestMethod.GET)
    public String getMaxAndMinValue(@RequestParam Long daysCounter, @RequestParam String currency){
        return currencyService.getMaxAndMin(daysCounter,currency);
    }

    @ApiOperation(value = "Provide major difference",notes = "Provide the major difference between the buy and ask rate ")
    @RequestMapping(value = "difference",params = {"daysCounter","currency"},method = RequestMethod.GET)
    public String getMajorDifference(@RequestParam Long daysCounter, @RequestParam String currency){
        return currencyService.getMajorDifference(daysCounter,currency);
    }
}
