package fr.kuro.kalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import fr.kuro.kalculator.API.CurrencyExchange;

public class CurrencyConverterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayList<String> mSymbols;
    private Spinner mCurrencySpinner1;
    private Spinner mCurrencySpinner2;
    private TextView mCurrencyValue1, mCurrencyValue2;
    private TextView mCurrencyName1, mCurrencyName2;
    private TextView mSelectedTextView;
    private HashMap<String, String> mCurrenciesList;

    private String currentExchangeSourceValue = "";
    private String currentExchangeTargetValue = "";

    // Just for check if a file of the list of all currencies was already created
    public static boolean isSymbolsFileAlreadyCreated(Activity activity) {
        File file = new File(activity.getFilesDir(), CurrencyExchange.CURRENCIES_SYMBOL_FILE);
        try {
            Reader fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        mCurrencySpinner1 = findViewById(R.id.currencies_list_1);
        mCurrencySpinner2 = findViewById(R.id.currencies_list_2);

        mCurrencyName1 = findViewById(R.id.currency_name_1);
        mCurrencyName2 = findViewById(R.id.currency_name_2);

        mCurrencyValue1 = findViewById(R.id.currency_value_1);
        mCurrencyValue2 = findViewById(R.id.currency_value_2);
        mSelectedTextView = mCurrencyValue1;

        mCurrencySpinner1.setOnItemSelectedListener(this);
        mCurrencySpinner2.setOnItemSelectedListener(this);

        CurrencyExchange currencyExchange = CurrencyExchange.getInstance();

        // If there is a problem for example

        File file = new File(getFilesDir(), CurrencyExchange.CURRENCIES_SYMBOL_FILE);
        try {
            Reader fileReader = new FileReader(file);
            Gson gson = new Gson();
            mCurrenciesList = gson.fromJson(fileReader, HashMap.class);
            mSymbols = new ArrayList<String>(mCurrenciesList.keySet());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mSymbols = currencyExchange.getCurrenciesSymbol();
        }
        if (mSymbols.isEmpty()) {
            mSymbols = currencyExchange.getCurrenciesSymbol();
        }

        // Just sort the symbol list
        Collections.sort(mSymbols);
        // Create an adapter for our spinner view which is in charge to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mSymbols);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mCurrencySpinner1.setAdapter(adapter);
        mCurrencySpinner2.setAdapter(adapter);

        // Default values for source and target
        if (!mSymbols.isEmpty()) {
            CurrencyExchange.sourceCurrency = mSymbols.get(0);
            CurrencyExchange.targetCurrency = mSymbols.get(0);
        }
    }

    public void launchCalculator(View view) {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {

        if (adapterView.equals(mCurrencySpinner1)) {
            mCurrencyName1.setText(mCurrenciesList.get(adapterView.getSelectedItem().toString()));
        } else if (adapterView.equals(mCurrencySpinner2)) {
            mCurrencyName2.setText(mCurrenciesList.get(adapterView.getSelectedItem().toString()));
        }
        update();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void updateExchangeSourceLayout(View view) {
        TextView tmp = mSelectedTextView;
        mSelectedTextView = ((TextView) ((LinearLayout) view).getChildAt(0));

        if (!mSelectedTextView.equals(tmp)) {
            currentExchangeSourceValue = "";
            updateTextColor(tmp);
        }
        update();
    }

    public void getElement(View view) {
        String elt = ((TextView) view).getText().toString();
        if ((elt.equals(".") && !currentExchangeSourceValue.contains(".")) || !elt.equals(".")) {
            currentExchangeSourceValue += elt;
        }
        if (currentExchangeSourceValue.startsWith("0")
                && currentExchangeSourceValue.length() > 1
                && currentExchangeSourceValue.toCharArray()[1] != '.') {

            currentExchangeSourceValue = currentExchangeSourceValue.substring(1);
        }else if (currentExchangeSourceValue.startsWith(".")) {
            currentExchangeSourceValue = "0" + currentExchangeSourceValue;
        }
        if (currentExchangeSourceValue.length() > 0) {
            updateCurrencyValues();
        }

    }

    public void clear(View view) {
        currentExchangeSourceValue = "";
        currentExchangeTargetValue = "";
        mCurrencyValue1.setText("0");
        mCurrencyValue2.setText("O");
    }

    public void delete(View view) {
        if (currentExchangeSourceValue.length() > 1) {
            currentExchangeSourceValue = currentExchangeSourceValue.substring(0, currentExchangeSourceValue.length() - 1);
            updateCurrencyValues();
        } else
            clear(view);
    }

    private void updateCurrencyValues() {
        mSelectedTextView.setText(currentExchangeSourceValue);
        if (mSelectedTextView.equals(mCurrencyValue1)) {
            mCurrencyValue2.setText(getCurrentExchangeTargetValue());
        } else if (mSelectedTextView.equals(mCurrencyValue2)) {
            mCurrencyValue1.setText(getCurrentExchangeTargetValue());
        }
    }

    private void update() {
        Thread thread = new Thread(CurrencyExchange.getInstance());
        // We want the new exchange rate
        CurrencyExchange.action = CurrencyExchange.RequestType.LIVE.value;
        String currentCurrencyFromSpinner1 = mCurrencySpinner1.getSelectedItem().toString();
        String currentCurrencyFromSpinner2 = mCurrencySpinner2.getSelectedItem().toString();

        // Check if an update of the currency rate is needed
        boolean updateRate = !CurrencyExchange.sourceCurrency.equals(currentCurrencyFromSpinner1)
                || !CurrencyExchange.targetCurrency.equals(currentCurrencyFromSpinner2);

        if (mSelectedTextView.equals(mCurrencyValue1)) {
            CurrencyExchange.sourceCurrency = currentCurrencyFromSpinner1;
            CurrencyExchange.targetCurrency = currentCurrencyFromSpinner2;
            thread.start(); // call method getNewExchangeRate()
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!currentExchangeSourceValue.equals("")) {
                mCurrencyValue1.setText(currentExchangeSourceValue);
                mCurrencyValue2.setText(getCurrentExchangeTargetValue());
            } else {
                mCurrencyValue1.setText("1");
                mCurrencyValue2.setText(String.valueOf(CurrencyExchange.exchangeRate));
            }

        } else if (mSelectedTextView.equals(mCurrencyValue2)) {

            CurrencyExchange.sourceCurrency = currentCurrencyFromSpinner2;
            CurrencyExchange.targetCurrency = currentCurrencyFromSpinner1;
            thread.start(); // call method getNewExchangeRate()
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!currentExchangeSourceValue.equals("")) {
                mCurrencyValue2.setText(currentExchangeSourceValue);
                mCurrencyValue1.setText(getCurrentExchangeTargetValue());
            } else {
                mCurrencyValue2.setText("1");
                mCurrencyValue1.setText(String.valueOf(CurrencyExchange.exchangeRate));
            }
        }
    }

    private void updateTextColor(TextView other) {
        mSelectedTextView.setTextColor(ContextCompat.getColor(this, R.color.orange));
        other.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    public String getCurrentExchangeTargetValue() {
        setCurrentExchangeTargetValue();
        return currentExchangeTargetValue;
    }

    public void setCurrentExchangeTargetValue() {
        this.currentExchangeTargetValue = String.valueOf(CurrencyExchange.exchangeRate * Float.parseFloat(this.currentExchangeSourceValue));
    }

}