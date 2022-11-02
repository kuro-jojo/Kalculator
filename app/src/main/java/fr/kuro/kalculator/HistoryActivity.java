package fr.kuro.kalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.kuro.kalculator.Adapter.HistoryAdapter;
import fr.kuro.kalculator.Dialog.ClearDialog;

public class HistoryActivity extends AppCompatActivity implements ClearDialog.BottomSheetListener {

    private ListView historyListView;
    private ArrayList<Map.Entry<String, String>> historyList;
    //private ArrayList<Map.Entry<String, String>> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.history_list);

        ImageButton trashBtn = findViewById(R.id.history_trash_btn);
        if (MainActivity.historyList.isEmpty())
            trashBtn.setVisibility(View.INVISIBLE);
        else
            trashBtn.setVisibility(View.VISIBLE);
        // Intent historyIntent = getIntent();
        // HashMap<String, String> history = (HashMap<String, String>) historyIntent.getSerializableExtra("history");

        // Create items from adapter
        historyList = new ArrayList<>(MainActivity.historyList.entrySet());
        historyListView.setAdapter(new HistoryAdapter(this, historyList));

    }

    public void clearHistory(View view) {

        ClearDialog clearDialog = new ClearDialog();
        clearDialog.show(getSupportFragmentManager(), "clearBottomSheet");
    }

    public void launchCalculator(View view) {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
    }

    @Override
    public void onClearButtonClicked() {
        MainActivity.historyList.clear();
        historyListView.setAdapter(new HistoryAdapter(this, historyList));

        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
    }
}