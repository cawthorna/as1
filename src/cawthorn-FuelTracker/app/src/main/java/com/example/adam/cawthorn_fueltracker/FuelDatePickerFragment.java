package com.example.adam.cawthorn_fueltracker;

import android.app.DatePickerDialog;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Created by Adam on 2016-01-06.
 *
 */
public class FuelDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        // Do something on date change.
        // Do nothing for now. Probably with intents in the future.
        //cant do this apparently. TextView textView = (TextView) findViewById(R.id.date_textView_add_entry);
        // datepicker listener TODO: implement this
    }

}
