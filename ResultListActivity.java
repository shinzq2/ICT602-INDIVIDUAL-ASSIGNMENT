package com.example.labia_zuhairah;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class ResultListActivity extends AppCompatActivity {

    ListView listView;
    DBHelper dbHelper;
    ArrayList<Integer> sortedIdList;
    ArrayList<String> sortedDisplayList;

    // Fixed month order for sorting
    final List<String> monthOrder = Arrays.asList(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Saved Records");
        setContentView(R.layout.activity_result_list);

        listView = findViewById(R.id.listView_results);
        dbHelper = new DBHelper(this);

        sortedDisplayList = new ArrayList<>();
        sortedIdList = new ArrayList<>();

        loadData();

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            int selectedId = sortedIdList.get(position);
            Intent intent = new Intent(ResultListActivity.this, DetailActivity.class);
            intent.putExtra("record_id", selectedId);
            startActivity(intent);
        });
    }

    private void loadData() {
        Cursor cursor = dbHelper.getAllData();

        if (cursor.getCount() == 0) {
            sortedDisplayList.add("No records found.");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, sortedDisplayList);
            listView.setAdapter(adapter);
            return;
        }

        // Temporary list to hold unsorted data
        ArrayList<Map<String, Object>> tempList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String month = cursor.getString(1);
            double finalCost = cursor.getDouble(5);

            Map<String, Object> item = new HashMap<>();
            item.put("id", id);
            item.put("month", month);
            item.put("finalCost", finalCost);
            tempList.add(item);
        }

        // Sort by month based on our fixed monthOrder
        tempList.sort(Comparator.comparingInt(
                o -> monthOrder.indexOf(((String) o.get("month"))))
        );

        // Populate final sorted lists
        for (Map<String, Object> item : tempList) {
            String month = (String) item.get("month");
            double finalCost = (double) item.get("finalCost");
            int id = (int) item.get("id");

            sortedDisplayList.add(month + " - RM " + String.format("%.2f", finalCost));
            sortedIdList.add(id);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, sortedDisplayList);
        listView.setAdapter(adapter);
    }
}
