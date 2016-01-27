package com.example.adam.cawthorn_fueltracker;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adam on 2016-01-06.
 *
 */
public class ListViewArrayAdapter extends ArrayAdapter<FuelLogEntry> {

    private final Context context;
    private final ArrayList<FuelLogEntry> FuelLogList;

    public ListViewArrayAdapter(Context context, ArrayList<FuelLogEntry> FuelLogList){
        super(context,-1,FuelLogList);
        this.context = context;
        this.FuelLogList = FuelLogList;
    }

    /**
     * Builds the FuelLogEntry display objects for the ListView.
     * @param position
     * @param convertView
     * @param parent
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       if(convertView == null) {
           LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           convertView = inflater.inflate(R.layout.listview_item, parent, false);
       }
        // Populate Date
        TextView textView = (TextView) convertView.findViewById(R.id.listview_textviewDate);
        textView.setText(FuelLogList.get(position).getDate());

        // Populate Amount
        textView = (TextView) convertView.findViewById(R.id.listview_textviewAmount);
        textView.setText(FuelLogList.get(position).getFormattedAmount());

        // Populate Cost
        textView = (TextView) convertView.findViewById(R.id.listview_textviewCost);
        textView.setText(FuelLogList.get(position).getCost());

        // Populate Fuel Grade
        textView = (TextView) convertView.findViewById(R.id.listview_textviewFuelGrade);
        textView.setText(FuelLogList.get(position).getGrade());

        // Populate Odometer
        textView = (TextView) convertView.findViewById(R.id.listview_textviewOdometer);
        textView.setText(FuelLogList.get(position).getFormattedOdometer());

        // Populate Station
        textView = (TextView) convertView.findViewById(R.id.listview_textviewStation);
        textView.setText(FuelLogList.get(position).getStation());

        // Populate Unit Cost
        textView = (TextView) convertView.findViewById(R.id.listview_textviewUnitCost);
        textView.setText(FuelLogList.get(position).getFormattedUnitCost());

        return convertView;
    }



}
