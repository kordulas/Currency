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
    private LocalDate todayDate = LocalDate.now();

    public String getAvgPrice(String date, String currency) {
        String mid = "";
        String checkedCurrency = validateCurrency(currency);
        LocalDate userDate = parseStringToDate(date);
        DayOfWeek dayOfWeek = userDate.getDayOfWeek();
        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            mid = getMidValue(checkedCurrency, userDate, mid);
        }else
            throw new WeekendDayException("Select a day of the week other than the weekend");
        return "For chosen currency :" + checkedCurrency + ", on day :" + date + ", average exchange rate was :" + mid;
    }

    public String getMaxAndMin(Long counter, String currency) {
        Long validateCounter = validateCounter(counter);
        String checkedCurrency = validateCurrency(currency);
        LocalDate startDate = todayDate.minusDays(validateCounter);
        String allData = correctURLCreator(checkedCurrency, todayDate, startDate, 'a');
        List<String> values = getAllValuesOfCustomerCurrencyByDate(allData);
        return "For currency : " + checkedCurrency + " " + getMaxAndMinValue(values) + " from last : " + validateCounter + " days";
    }

    public String getMajorDifference(Long counter, String currency) {
        Long validateCounter = validateCounter(counter);
        String checkedCurrency = validateCurrency(currency);
        LocalDate startDate = todayDate.minusDays(validateCounter);
        String allData = correctURLCreator(checkedCurrency, todayDate, startDate,'c');
        List<String> valuesOfBidsAndAsks = getValuesOfBidsAndAsks(allData);
        return "Major difference :" + valuesOfBidsAndAsks.get(0) + " ," + " minor difference :" + valuesOfBidsAndAsks.get(1)
                + " from last : " + validateCounter + " days";
    }
    /** Method to check if our counter has correct value
     * @param counter
     * @return checked counter if is correct, otherwise throw exception.
     */
    private Long validateCounter(Long counter) {
        if(counter > 255){
            throw new CounterException("Designated range + " + counter + " is to high, max counter is 255");
        }
        else return counter;
    }
    /** Method which return average exchange value  .
     * @param checkedCurrency, userDate, dayOfWeek
     * @return prepared and ready to return statement , if there is some error connected with wrong data method will throw
     * one of defined exception depend on made mistake.
     */
    private String getMidValue(String checkedCurrency, LocalDate userDate, String mid) {
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
        return mid;
    }

    /** Method to parse introduced date in string to Localdate class format
     * @param date
     * @return checked Localdate object, otherwise throw exception.
     */
    private LocalDate parseStringToDate(String date) {
        LocalDate userDate;
        try {
            userDate = LocalDate.parse(date);
        } catch (Exception e) {
            throw new WrongDataException("You type incorrect data format, use correctly pattern : yyyy-MM-dd");
        }
        return userDate;
    }
    /** Method to convert filled url with data chosen by user to list of two string with max and min value.
     * @param allData
     * @return list with max and min value for currency chosen by user, if there is problem with data throw exception.
     */
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
    /** Method to fill necessary Map to further extract
     * @param allData
     * @return checked map with correct values, otherwise throw exception.
     */
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
    /** Method to extract values presented later as whole statement.
     * @param values
     * @return part of further statement with extracted data.
     */
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
    /** Method to get all medium currency values
     * @param allData
     * @return list with all medium currency values, if there is mistake with data send by user throw exception.
     */
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
    /** Method to build correct url path based on NBP API and fill it with parameters delivered by user
     * @param currency, endDate, startDate, tabel
     * @return checked and correct url ready for extracting further data, otherwise false.
     */
    private String correctURLCreator(String currency, LocalDate endDate, LocalDate startDate, char tabel) {
        return restTemplate.getForObject(URL + "/{tabel}/{currency}/{startDate}/{endDate}/"
                , String.class, tabel, currency, startDate, endDate);
    }
    /** Method to check introduced currency
     * @param currency
     * @return checked currency if is correct, otherwise throw exception.
     */
    private String validateCurrency(String currency) {
        try {
            Currency checkedCurrency = Currency.getInstance(currency.toUpperCase());
            return checkedCurrency.toString();
        } catch (Exception e ) {
            throw new WrongCurrencyName("You type incorrectly currency value :" + " " + currency.toUpperCase());
        }
    }
}