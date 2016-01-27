package com.example.adam.cawthorn_fueltracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class editEntry extends ActionBarActivity  implements DatePickerDialog.OnDateSetListener {
    private FuelLogEntry fuelLogEntry;
    private int position;
    private int dateCode = 1;

    /** Overwritten onCreate method
     * what the activity is required to do on creation
     * @param savedInstanceState way to pass information in, not used in this subclass.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        // Get data from Intent
        Intent intent = getIntent();
        fuelLogEntry = (FuelLogEntry) intent.getSerializableExtra(MainActivity.INTENT_DATA);
        int requestCode = intent.getIntExtra(MainActivity.REQUEST_CODE, 0);

        // Set Date to default to the entries date and make it clickable.
        TextView textView = (TextView) findViewById(R.id.date_textView_add_entry);
        textView.setText(fuelLogEntry.getDate());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        // If we are editing an entry set all fields to the old values, otherwise leave blank.
        if(requestCode == MainActivity.EDIT_FUEL_ENTRY_OBJECT) {
            EditText editText = (EditText) findViewById(R.id.station_edittext);
            editText.setText(fuelLogEntry.getStation());
            editText = (EditText) findViewById(R.id.odometer_edittext);
            editText.setText(fuelLogEntry.getOdometer());
            editText = (EditText) findViewById(R.id.grade_edittext);
            editText.setText(fuelLogEntry.getGrade());
            editText = (EditText) findViewById(R.id.amount_edittext);
            editText.setText(fuelLogEntry.getAmount());
            editText = (EditText) findViewById(R.id.unit_cost_edittext);
            editText.setText(fuelLogEntry.getUnitCost());

            // Get position so it can be sent back for proper placement.
            position = intent.getIntExtra(MainActivity.FUEL_LOG_LIST_POSITION, -1);

        }
    }

    /** Overwritten onBackPressed method
     * changes back button behaviour to be the same as clicking the cancel button.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.FUEL_LOG_LIST_POSITION, position);
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
    }

    /**
     * shows the custom built DatePickerDialog in the view provided.
     * @param view
     */
    public void showDatePickerDialog(View view) {
        FuelDatePickerFragment newFragment = new FuelDatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");

    }

    /**
     * onClick method called by clicking the confirm button.
     * Validates all information in the required fields.
     * @param view
     */
    public void confirmAddition(View view) {
        EditText editText;

        // Check to see if date is valid
        if(dateCode < 0) {
            if(dateCode == -4) {
                Toast.makeText(this,"Error: Date is after today",Toast.LENGTH_SHORT).show();
            } else if(dateCode == -1) {
                Toast.makeText(this,"Error: Invalid year.",Toast.LENGTH_SHORT).show();
            } else if(dateCode == -2) {
                Toast.makeText(this,"Error: Invalid month.",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"Error: Invalid day.",Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Check Odometer to see if it is valid. (not blank and > 0)
        editText = (EditText) findViewById(R.id.odometer_edittext);
        if(editText.getText().toString().length() > 0) {
            if(!fuelLogEntry.setOdometer(Double.parseDouble(editText.getText().toString()))) {
                Toast.makeText(this,"Error: Odometer is too small.",Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this,"Error: Odometer cannot be blank.",Toast.LENGTH_SHORT).show();
            return;
        }

        // Check Amount to see if it is valid. (not blank and > 0)
        editText = (EditText) findViewById(R.id.amount_edittext);
        if(editText.getText().toString().length() > 0) {
            if(!fuelLogEntry.setAmount(Double.parseDouble(editText.getText().toString()))) {
                Toast.makeText(this,"Error: Invalid amount.",Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this,"Error: Amount cannot be blank.",Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if Station is valid. (not blank)
        editText= (EditText) findViewById(R.id.station_edittext);
        if(!fuelLogEntry.setStation(editText.getText().toString().trim())) {
            Toast.makeText(this,"Error: Station cannot be blank.",Toast.LENGTH_SHORT).show();
            return;
        }

        // Check Unit Cost to see if it is valid. (not blank and > 0)
        editText = (EditText) findViewById(R.id.unit_cost_edittext);
        if(editText.getText().toString().length() > 0) {
            if(!fuelLogEntry.setUnitCost(Double.parseDouble(editText.getText().toString()))) {
                Toast.makeText(this,"Error: Invalid unit cost.",Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this,"Error: Unit cost cannot be blank.",Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if Fuel Grade is valid. (not blank)
        editText = (EditText) findViewById(R.id.grade_edittext);
        if(!fuelLogEntry.setGrade(editText.getText().toString().trim())) {
            Toast.makeText(this,"Error: Grade cannot be blank.",Toast.LENGTH_SHORT).show();
            return;
        }

        // everything above is good, finish activity.
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_DATA, fuelLogEntry);
        intent.putExtra(MainActivity.FUEL_LOG_LIST_POSITION, position);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * onClick method for the cancel button.
     * Cancels the addition or edit.
     * @param view
     */
    public void cancelAddition(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.FUEL_LOG_LIST_POSITION, position);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    /**
     * method called by the closure of the DatePickerDialog.
     * @param view
     * @param year
     * @param month
     * @param day
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {

        dateCode = fuelLogEntry.setDate(year, month, day);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(year);
        stringBuilder.append('-');
        if(month + 1 < 10) stringBuilder.append('0');
        stringBuilder.append(month + 1);
        stringBuilder.append('-');
        if(day < 10) stringBuilder.append('0');
        stringBuilder.append(day);

        TextView textView = (TextView) findViewById(R.id.date_textView_add_entry);
        textView.setText(stringBuilder.toString());

    }
}
