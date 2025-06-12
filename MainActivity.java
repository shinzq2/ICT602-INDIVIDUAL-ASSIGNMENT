package com.example.labia_zuhairah;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText editUnit, editRebate;
    TextView textTotal, textFinal;
    Button buttonCalculate, buttonSave, buttonView;

    double totalCharges = 0;
    double finalCost = 0;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Utility Budget");
        setContentView(R.layout.activity_main);

        // Link UI elements
        spinnerMonth = findViewById(R.id.spinner_month);
        editUnit = findViewById(R.id.edit_unit);
        editRebate = findViewById(R.id.edit_rebate);
        textTotal = findViewById(R.id.text_total);
        textFinal = findViewById(R.id.text_final);
        buttonCalculate = findViewById(R.id.button_calculate);
        buttonSave = findViewById(R.id.button_save);
        buttonView = findViewById(R.id.button_view);
        Button aboutBtn = findViewById(R.id.button_about);

        // Button listeners
        buttonCalculate.setOnClickListener(view -> calculateCharges());
        buttonSave.setOnClickListener(view -> saveToDatabase());
        buttonView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ResultListActivity.class);
            startActivity(intent);
        });
        aboutBtn.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
        });

        // Initialize DB helper
        dbHelper = new DBHelper(this);

        // Populate spinner with months
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        // Calculate button
        buttonCalculate.setOnClickListener(view -> {
            calculateCharges();
        });

        // Save to DB
        buttonSave.setOnClickListener(view -> {
            saveToDatabase();
        });

        // View Records
        buttonView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ResultListActivity.class);
            startActivity(intent);
        });
    }

    private void calculateCharges() {
        String unitStr = editUnit.getText().toString().trim();
        String rebateStr = editRebate.getText().toString().trim();

        if (unitStr.isEmpty() || rebateStr.isEmpty()) {
            showMessage("Error", "Please enter both unit used and rebate %");
            return;
        }

        double unit = Double.parseDouble(unitStr);
        double rebate = Double.parseDouble(rebateStr);

        if (rebate < 0 || rebate > 5) {
            showMessage("Invalid Rebate", "Rebate must be between 0% and 5%");
            return;
        }

        totalCharges = calculateTotal(unit);
        finalCost = totalCharges - (totalCharges * rebate / 100);

        textTotal.setText(String.format("Total Charges: RM %.2f", totalCharges));
        textFinal.setText(String.format("Final Cost after Rebate: RM %.2f", finalCost));
    }

    private double calculateTotal(double unit) {
        double total = 0;

        if (unit <= 200) {
            total = unit * 0.218;
        } else if (unit <= 300) {
            total = 200 * 0.218 + (unit - 200) * 0.334;
        } else if (unit <= 600) {
            total = 200 * 0.218 + 100 * 0.334 + (unit - 300) * 0.516;
        } else {
            total = 200 * 0.218 + 100 * 0.334 + 300 * 0.516 + (unit - 600) * 0.546;
        }

        return total;
    }

    private void saveToDatabase() {
        String month = spinnerMonth.getSelectedItem().toString();
        String unitStr = editUnit.getText().toString().trim();
        String rebateStr = editRebate.getText().toString().trim();

        if (unitStr.isEmpty() || rebateStr.isEmpty() || totalCharges == 0) {
            showMessage("Error", "Please calculate before saving.");
            return;
        }

        double unit = Double.parseDouble(unitStr);
        double rebate = Double.parseDouble(rebateStr);

        boolean inserted = dbHelper.insertData(month, unit, totalCharges, rebate, finalCost);

        if (inserted) {
            Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessage(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(msg).setPositiveButton("OK", null).show();
    }
}
