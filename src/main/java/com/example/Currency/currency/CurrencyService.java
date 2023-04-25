package com.example.Currency.currency;

import com.example.Currency.exception.CounterException;
import com.example.Currency.exception.WeekendDayException;
import com.example.Currency.exception.WrongCurrencyName;
import com.example.Currency.exception.WrongDataException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
public class CurrencyService {

    private final String URL = "http://api.nbp.pl/api/exchangerates/rates";
    private RestTemplate restTemplate = new RestTemplate();

    public String getAvgPrice(String date, String currency) {
        String checkedCurrency = validateCurrency(currency);
        LocalDate userDate = parseStringToDate(date);
        DayOfWeek dayOfWeek = userDate.getDayOfWeek();
        return getPreparedReturnStatement(date, checkedCurrency, userDate, dayOfWeek);
    }

    public String getMaxAndMin(Long counter, String currency) {
        Long validateCounter = validateCounter(counter);
        String checkedCurrency = validateCurrency(currency);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(validateCounter);
        String allData = correctURLCreator(checkedCurrency, endDate, startDate, 'a');
        List<String> values = getAllValuesOfCustomerCurrencyByDate(allData);
        return "For currency : " + checkedCurrency + " " + getMaxAndMinValue(values) + " from last : " + validateCounter + " days";
    }

    public String getMajorDifference(Long counter, String currency) {
        Long validateCounter = validateCounter(counter);
        String checkedCurrency = validateCurrency(currency);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(validateCounter);
        String allData = correctURLCreator(checkedCurrency, endDate, startDate,'c');
        List<String> valuesOfBidsAndAsks = getValuesOfBidsAndAsks(allData);
        return "Major difference :" + valuesOfBidsAndAsks.get(0) + " ," + " minor difference :" + valuesOfBidsAndAsks.get(1)
                + " from last : " + validateCounter + " days";
    }

    private Long validateCounter(Long counter) {
        if(counter > 255){
            throw new CounterException("Designated range + " + counter + " is to high, max counter is 255");
        }
        else return counter;
    }

    private String getPreparedReturnStatement(String date, String checkedCurrency, LocalDate userDate, DayOfWeek dayOfWeek) {
        String mid = "";
        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            String allData = restTemplate.getForObject(URL + "/a/{checkedCurrency}/{userDate}/"
                    , String.class, checkedCurrency, userDate);
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JSONObject jsonObject = new JSONObject(allData);
                JSONArray records = jsonObject.getJSONArray("rates");
                JsonNode jsonNode = objectMapper.readTree(records.toString());
                for (JsonNode node : jsonNode) {
                    mid = node.findValue("mid").toString();
                }
            } catch (Exception e) {
                throw new WrongDataException("Application couldn't find one of chosen value");
            }
            return "For chosen currency :" + checkedCurrency + ", on day :" + date + ", average exchange rate was :" + mid;
        } else
            throw new WeekendDayException("Select a day of the week other than the weekend");
    }

    private LocalDate parseStringToDate(String date) {
        LocalDate userDate;
        try {
            userDate = LocalDate.parse(date);
        } catch (Exception e) {
            throw new WrongDataException("You type incorrect data format, use correctly pattern : yyyy-MM-dd");
        }
        return userDate;
    }

    private List<String> getValuesOfBidsAndAsks(String allData) {
        Map<String, BigDecimal> values = fillMapWithValues(allData);
        Optional<Map.Entry<String, BigDecimal>> max = values.entrySet().stream()
                .max(Map.Entry.comparingByValue());
        Optional<Map.Entry<String, BigDecimal>> min = values.entrySet().stream()
                .min(Map.Entry.comparingByValue());
        if(max.isPresent() && min.isPresent()) {
            return List.of(max.get().toString(), min.get().toString());
        }else
            throw new WrongDataException("Application couldn't find one of chosen value");
    }

    private Map<String, BigDecimal> fillMapWithValues(String allData) {
        Map<String, BigDecimal> values = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject jsonObject = new JSONObject(allData);
            JSONArray records = jsonObject.getJSONArray("rates");
            JsonNode jsonNode = objectMapper.readTree(records.toString());
            for (JsonNode node : jsonNode) {
                JsonNode date = node.findValue("effectiveDate");
                JsonNode bid = node.findValue("bid");
                JsonNode ask = node.findValue("ask");
                BigDecimal subtract = BigDecimal.valueOf(ask.asDouble()).subtract(BigDecimal.valueOf(bid.asDouble()));
                values.put(date.toString(),subtract);
            }
        } catch (Exception e) {
            throw new WrongDataException("You type incorrect data");
        }
        return values;
    }

    private String getMaxAndMinValue(List<String> values) {
        List<Double> numbers = new ArrayList<>();
        for (String value : values) {
            double number = Double.parseDouble(value);
            numbers.add(number);
        }
        double max = numbers.stream()
                .mapToDouble(number -> number)
                .max()
                .orElse(Double.MIN_VALUE);
        double min = numbers.stream()
                .mapToDouble(number -> number)
                .min()
                .orElse(Double.MIN_VALUE);
        return "max value was : " + max + " , min value was : " + min;
    }

    private List<String> getAllValuesOfCustomerCurrencyByDate(String allData) {
        List<String> values = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(allData);
            JsonNode jsonNode1 = jsonNode.get("rates");
            for (JsonNode node : jsonNode1) {
                JsonNode mid = node.findValue("mid");
                values.add(String.valueOf(mid));
            }
        } catch (JsonProcessingException e) {
            throw new WrongDataException("You type incorrect data");
        }
        return values;
    }

    private String correctURLCreator(String currency, LocalDate endDate, LocalDate startDate, char tabel) {
        return restTemplate.getForObject(URL + "/{tabel}/{currency}/{startDate}/{endDate}/"
                , String.class, tabel, currency, startDate, endDate);
    }

    private String validateCurrency(String currency) {
        try {
            Currency checkedCurrency = Currency.getInstance(currency.toUpperCase());
            return checkedCurrency.toString();
        } catch (Exception e ) {
            throw new WrongCurrencyName("You type incorrectly currency value :" + " " + currency.toUpperCase());
        }
    }
}