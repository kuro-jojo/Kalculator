package fr.kuro.kalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import fr.kuro.kalculator.API.CurrencyExchange;

public class MainActivity extends AppCompatActivity {

    private static final String DIVISION_BY_ZERO = "Division by zero";
    public static HashMap<String, String> historyList;
    public static Context context;
    private TextView calculationResultView;
    private TextView calculationView;
    private HashMap<String, String> operators;
    private ArrayList<String> calculation;
    private String finalResult = "0";
    private boolean gotResult = true;
    private ImageButton currencyBtn;
    private ImageButton historyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        // Using a singleton here
        Thread currencyThread = new Thread(CurrencyExchange.getInstance());
        // CurrencyExchange.action = "LIST";
        // Make an API request only if we didn't do it before
        if (!CurrencyConverterActivity.isSymbolsFileAlreadyCreated(this)) {
            currencyThread.start();
        }

        calculation = new ArrayList<String>();
        operators = new HashMap<String, String>();
        historyList = new HashMap<String, String>();
        currencyBtn = findViewById(R.id.currency_activity);
        historyBtn = findViewById(R.id.history_activity);

        currencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent currencyIntent = new Intent(getApplicationContext(), CurrencyConverterActivity.class);
                startActivity(currencyIntent);
            }
        });
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent historyIntent = new Intent(getApplicationContext(), HistoryActivity.class);
                //historyIntent.putExtra("history", history);
                startActivity(historyIntent);
            }
        });
        operators.put("add", "+");
        operators.put("subs", "-");
        operators.put("div", "\u00F7");
        operators.put("multi", "\u00D7");
        operators.put("per", "%");

        calculation.add("0");
        calculationView = findViewById(R.id.calculation);
        calculationResultView = findViewById(R.id.calculation_result);
    }

    public void getElement(View view) {

        // Do nothing if the previous operation results in an exception
        if (!calculation.contains(DIVISION_BY_ZERO)) {
            String elt = String.join("", ((Button) view).getText());
            int lastIndex = getLastIndex();
            String lastElement = getLastElement();
            int size = calculation.size();

            if (elt.equals(".")) { // manage float numbers
                if (operators.containsValue(lastElement)) // last element is an operator
                    calculation.add("0" + elt);
                else if (!lastElement.contains(elt)) // last element isn't a float
                    calculation.set(lastIndex, lastElement + elt);
            } else if (size == 1 && calculation.get(0).equals("0") && (elt.equals("0") || elt.matches("\\d"))) {
                calculation.remove(0);
                calculation.add(elt);
            } else if (operators.containsValue(elt) && operators.containsValue(lastElement)) {
                calculation.set(lastIndex, elt);
            } else if (!lastElement.equals("0")) {
                if (elt.matches("\\d") && lastElement.matches("(^\\d+\\.?\\d*$)")) { // concatenate numbers
                    calculation.set(lastIndex, lastElement + elt);
                } else calculation.add(elt);
            } else calculation.add(elt);
            updateResult(); // lastIndex has changed
        }

        if (gotResult) gotResult = false;
    }

    private void updateResult() {

        int lastIndex = getLastIndex();
        String lastInput = getLastElement();

        calculationView.setText(String.join("", calculation));

        if (!operators.containsValue(lastInput))
            finalResult = getResult((ArrayList<String>) calculation.clone());
        else finalResult = getResult(new ArrayList<>(calculation.subList(0, lastIndex)));
        calculationResultView.setText("= " + finalResult);
    }

    public void finalResult(View view) {
        if (!gotResult) {
            updateResult();
            saveHistory();
            gotResult = true;
        }
    }

    public void clear(View view) {
        clearCalculation();
        calculationView.setText("0");
        calculationResultView.setText("= 0");
    }

    public void delete(View view) {
        if (!calculation.isEmpty()) {
            if (calculation.get(getLastIndex()).length() > 1) {
                String tmp = calculation.get(getLastIndex());
                calculation.set(getLastIndex(), tmp.substring(0, tmp.length() - 1));
            } else
                calculation.remove(getLastIndex());
            if (calculation.isEmpty()) clear(view);
            updateResult();
        }
    }

    public void getPercentageOf(View view) {
        if (!operators.containsValue(getLastElement())) {
            float percentage = Float.valueOf(getLastElement()) / 100;
            calculation.set(getLastIndex(), String.valueOf(percentage));
            updateResult();
        }
    }

    public void saveHistory() {
        historyList.put(String.join("", calculation), finalResult);
        clearCalculation();
        System.out.println(historyList.toString());
    }

    private int getLastIndex() {
        return calculation.size() - 1;
    }

    private String getLastElement() {
        return calculation.get(getLastIndex());
    }

    private void clearCalculation() {
        calculation.clear();
        calculation.add("0");
    }

    /**
     * @param operations
     * @return
     */
    private String getResult(ArrayList<String> operations) {

        final String NEUTRAL_ELEMENT_0 = "0";
        final String NEUTRAL_ELEMENT_1 = "1";
        String res = "";

        if (operations.size() == 0) {
            // Do nothing
        } else if (operations.size() == 2) {
            if (operators.containsValue(operations.get(0))) // The expression starts with an operator
                operations.add(0, "0");

            if (operators.containsValue(operations.get(1))) { // The expression ends with an operator
                if (operations.get(0).equals(operators.get("multi")) || operations.get(0).equals(operators.get("div")))
                    operations.add(0, NEUTRAL_ELEMENT_1);
                else if (operations.get(0).equals(operators.get("add")) || operations.get(0).equals(operators.get("subs")))
                    operations.add(0, NEUTRAL_ELEMENT_0);
            }
        } else {
            while (operations.size() != 1) {
                try {
                    calculate(operations, operators.get("multi"), operators.get("div"));
                    calculate(operations, operators.get("add"), operators.get("subs"));
                } catch (ArithmeticException e) {
                    System.out.println(e);
                    break;
                }
            }
        }
        if (operations.size() == 1) {
            if (Float.parseFloat(operations.get(0)) % 1 == 0)
                res = String.valueOf((int) (Float.parseFloat(operations.get(0))));
            else res = String.valueOf(Float.parseFloat(operations.get(0)));
        } else if (operations.contains(DIVISION_BY_ZERO)) res = DIVISION_BY_ZERO;

        // Change size of result and calculation views
        return res;
    }


    private void calculate(ArrayList<String> array, String op1, String op2) throws ArithmeticException {
        while (array.contains(op1) || array.contains(op2)) {

            if (array.contains(op1) && array.contains(op2)) { // Ordre de priorité
                if (array.indexOf(op1) < array.indexOf(op2)) {
                    performCalculation(array, op2);
                } else {
                    performCalculation(array, op2);
                }
            } else {
                if (array.contains(op1)) {
                    performCalculation(array, op1);
                } else if (array.contains(op2)) {
                    performCalculation(array, op2);
                }
            }
        }
    }

    private void performCalculation(ArrayList<String> array, String operator) throws ArithmeticException {

        float res = 0;
        int index = array.indexOf(operator);
        String error = "";
        float a = Float.parseFloat(array.get(index - 1));
        float b = Float.parseFloat(array.get(index + 1));

        switch (operator) {
            case "\u00D7":
                res = a * b;
                break;
            case "\u00F7":
                if (b == 0.) {
                    array.add(DIVISION_BY_ZERO);
                    throw new ArithmeticException("Division by zero");
                }
                res = a / b;
                break;
            case "+":
                res = a + b;
                break;
            case "-":
                res = a - b;
                break;
            default:
                throw new ArithmeticException("Operation not defined!!");

        }
        array.set(index, String.valueOf(res));
        array.remove(index + 1);
        array.remove(index - 1);

    }


}