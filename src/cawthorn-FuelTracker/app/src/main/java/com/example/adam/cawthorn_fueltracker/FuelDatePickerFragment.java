package com.example.adam.cawthorn_fueltracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by Adam on 2016-01-06.
 *  @return Dialog
 *  creates a DatePickerDialog and sets it to today's date, then returns.
 */
public class FuelDatePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),(editEntry)getActivity(), year, month, day);
    }
}
