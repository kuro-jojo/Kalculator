package fr.kuro.kalculator.API;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import fr.kuro.kalculator.MainActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyExchange implements Runnable {
    public static final String CURRENCIES_SYMBOL_FILE = "currenciesList.txt";
    public static float exchangeRate = 1;
    public static String sourceCurrency;
    public static String targetCurrency;
    public static String action = "list";
    private static CurrencyExchange currencyExchangeInstance;
    private final String API_KEY = "cNVZshBRcBZlnbP31rjnQGBejkuBZQMd";
    private final String URL = "https://api.apilayer.com/currency_data/";
    private final Context context;
    private ArrayList<String> currenciesSymbol = new ArrayList<>();
    private OkHttpClient httpClient;
    private Gson gson;
    // fields of the api response
    private HashMap<String, String> currencies;
    private HashMap<String, String> quotes;

    private CurrencyExchange(Context context) {
        httpClient = new OkHttpClient().newBuilder().build();
        gson = new Gson();
        this.context = context;
    }

    public static CurrencyExchange getInstance() {
        if (currencyExchangeInstance == null) {
            return new CurrencyExchange(MainActivity.context);
        }
        return currencyExchangeInstance;
    }

    public void getListOfCurrencies() throws IOException {
        action = "list";
        Request listRequest = new Request.Builder().url(URL + action).addHeader("apikey", API_KEY).build();

        Response response = httpClient.newCall(listRequest).execute();
        this.currencies = gson.fromJson(response.body().string(), CurrencyExchange.class).currencies;
        this.currenciesSymbol = new ArrayList<>(currencies.keySet());

        File currenciesListFile = new File(context.getFilesDir(), CURRENCIES_SYMBOL_FILE);
        FileWriter fileWriter = new FileWriter(currenciesListFile);
        gson.toJson(currencies, fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }

    public void getNewExchangeRate() throws IOException {
        action = "live";

        Request request = new Request.Builder().url(URL + action + "?source=" + sourceCurrency + "&currencies=" + targetCurrency).addHeader("apikey", API_KEY).build();
        Response response = httpClient.newCall(request).execute();
        String r = response.body().string();
        quotes = gson.fromJson(r, CurrencyExchange.class).quotes;

        // change the actual exchange rate
        if (!quotes.isEmpty()) {
            exchangeRate = Float.valueOf((String) quotes.values().toArray()[0]);
        }
    }

    @Override
    public void run() {

        try {
            switch (action) {
                case "list":
                    getListOfCurrencies();
                    break;
                case "live":
                    getNewExchangeRate();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public HashMap<String, String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(HashMap<String, String> currencies) {
        this.currencies = currencies;
    }

    public ArrayList<String> getCurrenciesSymbol() {
        return currenciesSymbol;
    }

    public void setCurrenciesSymbol(ArrayList<String> currenciesSymbol) {
        this.currenciesSymbol = currenciesSymbol;
    }

    public HashMap<String, String> getQuotes() {
        return quotes;
    }

    public void setQuotes(HashMap<String, String> quotes) {
        this.quotes = quotes;
    }

    public enum RequestType {

        LIVE("live"),
        LIST("list");
        public final String value;

        RequestType(String value) {
            this.value = value;
        }

    }
}
