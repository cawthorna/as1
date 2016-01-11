package com.example.adam.cawthorn_fueltracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

public class addEntry extends ActionBarActivity {

    private int year, month, day;
    private String station;
    private int odometer;
    private String grade;
    private int amount;
    private int unitCost;
    private int cost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

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

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new FuelDatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void confirmAddition(View view) {

        // TODO: this

    }

    public void cancelAddition(View view) {
        finish();
    }

}
