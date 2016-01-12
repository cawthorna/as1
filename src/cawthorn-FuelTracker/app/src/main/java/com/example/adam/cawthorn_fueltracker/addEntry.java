package com.example.adam.cawthorn_fueltracker;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;

public class addEntry extends ActionBarActivity  implements DatePickerDialog.OnDateSetListener {

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
        FuelDatePickerFragment newFragment = new FuelDatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");

    }

    public void confirmAddition(View view) {
        // TODO: this
        // save to file and return.
        EditText editText = (EditText) findViewById(R.id.station_edittext);
        station = editText.getText().toString();
        editText = (EditText) findViewById(R.id.odometer_edittext);
        odometer = Integer.getInteger(editText.getText().toString(), 0);
        editText = (EditText) findViewById(R.id.grade_edittext);
        grade = editText.getText().toString();
        editText = (EditText) findViewById(R.id.amount_edittext);
        amount = Integer.getInteger(editText.getText().toString(), 0);
        editText = (EditText) findViewById(R.id.unit_cost_edittext);
        unitCost = Integer.getInteger(editText.getText().toString(), 0);

        FuelLogEntry newEntry = new FuelLogEntry(year, month, day, station, odometer, grade, amount, unitCost);
        ArrayList<FuelLogEntry> FuelLogList = loadFuelLog();
        FuelLogList.add(newEntry);
        try {
            File file = new File(getFilesDir(), MainActivity.FUELLOGFILE);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            ListIterator<FuelLogEntry> iterator = FuelLogList.listIterator();
            while(iterator.hasNext()) {
                outputStream.writeObject(iterator.next());
            }
            outputStream.close();
            fileOutputStream.close();

        } catch (IOException exc) {
            exc.printStackTrace();
        }
        finish();
        // TODO: find a way to pass an object back.

    }

    public void cancelAddition(View view) {
        finish();
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;

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

    private ArrayList<FuelLogEntry> loadFuelLog() {
        ArrayList<FuelLogEntry> FuelLogList = new ArrayList<FuelLogEntry>();

        try {
            File file = new File(getFilesDir(), MainActivity.FUELLOGFILE);
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            try {
                while (FuelLogList.add((FuelLogEntry) inputStream.readObject())) ;
            } catch (EOFException ex) {
                // do nothing, reached end of the file.
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found exception");
            return new ArrayList<FuelLogEntry>();


        } catch (IOException ex) {
            System.out.println("IO Exception");
            ex.printStackTrace();

        } catch (ClassNotFoundException ex) {
            System.out.println("FuelLogEntry class not found");
            ex.printStackTrace();
        }
        return FuelLogList;
    }

}
