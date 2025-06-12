package com.example.labia_zuhairah;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    TextView detailText;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Detail View");
        setContentView(R.layout.activity_detail);

        detailText = findViewById(R.id.text_detail);
        dbHelper = new DBHelper(this);

        int recordId = getIntent().getIntExtra("record_id", -1);

        if (recordId != -1) {
            loadDetail(recordId);
        } else {
            detailText.setText("Error loading record.");
        }
    }

    private void loadDetail(int id) {
        Cursor cursor = dbHelper.getDataById(id);
        if (cursor.moveToFirst()) {
            String month = cursor.getString(1);
            double unit = cursor.getDouble(2);
            double total = cursor.getDouble(3);
            double rebate = cursor.getDouble(4);
            double finalCost = cursor.getDouble(5);

            String info = "Month: " + month +
                    "\nUnit Used: " + unit +
                    "\nTotal Charges: RM " + String.format("%.2f", total) +
                    "\nRebate: " + rebate + "%" +
                    "\nFinal Cost: RM " + String.format("%.2f", finalCost);

            detailText.setText(info);
        }
    }
}
