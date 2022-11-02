package fr.kuro.kalculator.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import fr.kuro.kalculator.R;

public class HistoryAdapter extends BaseAdapter {
    // Info about the app, activity
    private Context context;
    private ArrayList<Map.Entry<String, String>> historyList;
    private LayoutInflater inflater;

    public HistoryAdapter(Context context, ArrayList<Map.Entry<String, String>> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int index) {
        return historyList.get(index);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.adapter_history, null);

        String operation = historyList.get(i).getKey();
        String result = historyList.get(i).getValue();


        TextView operationView = view.findViewById(R.id.history_operation);
        TextView resultView = view.findViewById(R.id.history_result);

        operationView.setText(operation);
        resultView.setText("=" + result);

        return view;
    }
}
