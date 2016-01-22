package com.example.adam.cawthorn_fueltracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity {

    protected final static String FUEL_LOG_STORE = "fuel_log_store";
    protected final static String INTENT_DATA = "FuelLogEntry";
    protected final static String REQUEST_CODE = "requestCode";
    protected final static String FUEL_LOG_LIST_POSITION = "fuelLogListPosition";
    static final int NEW_FUEL_ENTRY_OBJECT = 1;
    static final int EDIT_FUEL_ENTRY_OBJECT = 2;

    protected ArrayList<FuelLogEntry> FuelLogList = new ArrayList<FuelLogEntry>();
    protected ListViewArrayAdapter adapter;
    protected ListView listView;
    protected int total_cost;
    protected int total_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         setTitle("Fuel Tracker");

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                listItemClicked(view, position);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        loadFuelLog();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveFuelLog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Start add entry activity on click
        if(id == R.id.action_add_entry){
            Calendar cal = Calendar.getInstance();
            FuelLogEntry newEntry = new FuelLogEntry(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), "", 0, "", 0, 0);

            Intent intent = new Intent(this, editEntry.class);
            intent.putExtra(INTENT_DATA,newEntry);
            intent.putExtra(REQUEST_CODE,NEW_FUEL_ENTRY_OBJECT);

            startActivityForResult(intent, NEW_FUEL_ENTRY_OBJECT);
        }

        if(id == R.id.action_refresh) {
            //TODO: remove this.
            loadFuelLog();
            adapter.notifyDataSetChanged();
            Toast.makeText(this,"View Refreshed",Toast.LENGTH_SHORT).show();
        }

        if(id == R.id.action_resetFile) {
            // TODO: change this for release (move to settings)
            File file = new File(getFilesDir(), FUEL_LOG_STORE);
            if(file.exists()) file.delete();
            loadFuelLog();
            loadFuelLog();

            adapter.notifyDataSetChanged();
            Toast.makeText(this, "File rebuilt", Toast.LENGTH_SHORT).show();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this,"Not implemented yet. Maybe in the future though...",Toast.LENGTH_LONG).show();
            //TODO: implement this.
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == NEW_FUEL_ENTRY_OBJECT) {
            if(resultCode == RESULT_OK) {
                FuelLogList.add((FuelLogEntry) data.getSerializableExtra(INTENT_DATA));
                total_cost += ((FuelLogEntry) data.getSerializableExtra(INTENT_DATA)).getIntCost();
                total_amount += ((FuelLogEntry) data.getSerializableExtra(INTENT_DATA)).getIntAmount();
            }
        }
        else if(requestCode == EDIT_FUEL_ENTRY_OBJECT) {
            if(resultCode == RESULT_OK) {
                int index = data.getIntExtra(FUEL_LOG_LIST_POSITION, -1);
                if(index >= 0) {
                    FuelLogList.set(index, (FuelLogEntry) data.getSerializableExtra(INTENT_DATA));
                    total_cost += ((FuelLogEntry) data.getSerializableExtra(INTENT_DATA)).getIntCost();
                    total_amount += ((FuelLogEntry) data.getSerializableExtra(INTENT_DATA)).getIntAmount();
                }
            } else {
                int index = data.getIntExtra(FUEL_LOG_LIST_POSITION, -1);
                if(index >= 0) {
                    FuelLogEntry temp = (FuelLogEntry) listView.getItemAtPosition(index);
                    total_amount += temp.getIntAmount();
                    total_cost += temp.getIntCost();
                }
            }
        }
        updateHeader();
        saveFuelLog();
        adapter.notifyDataSetChanged();
    }

    private void loadFuelLog() {
        FuelLogList.clear();
        total_cost = 0;
        total_amount = 0;

        try {

            FileInputStream fileInputStream = openFileInput(FUEL_LOG_STORE);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            Gson gson = new Gson();
            // below from http://google-gson.googlecode.com/svn/tags/1.1.1/docs/javadocs/com/google/gson/reflect/TypeToken.html
            FuelLogList = gson.fromJson(bufferedReader, new TypeToken<ArrayList<FuelLogEntry>>() {}.getType());
            Toast.makeText(MainActivity.this,"Length: " + FuelLogList.size(), Toast.LENGTH_SHORT).show();
            bufferedReader.close();
            fileInputStream.close();



        } catch (FileNotFoundException ex) {

            // The file does not exist. create it and return an empty list.
            // should only happen first run.
            // add temporary objects for testing TODO: remove this
            System.out.println("File not found, creating new one. " + FUEL_LOG_STORE);
            for (int i = 0; i < 16; i++) {
                FuelLogList.add(new FuelLogEntry(2000 + i, 2, 3, "Abc", 200000.2 + i, "regular", 2, 100));
            }
            saveFuelLog();

        } catch(IOException  ex) {
            throw new RuntimeException();

        } finally {

            updateHeader();
            // Collections.sort(FuelLogList); Do not sort in accordance with assignment spec

            adapter = new ListViewArrayAdapter(this,FuelLogList);
            listView.setAdapter(adapter);

            Toast.makeText(MainActivity.this, "returning from load fuel log with " + FuelLogList.size() + " entries.",Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFuelLog() {

        try {
            FileOutputStream fileOutputStream = openFileOutput(FUEL_LOG_STORE, MODE_PRIVATE);
            BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            Gson gson = new Gson();
            gson.toJson(FuelLogList,outputStream);
            outputStream.close();
            fileOutputStream.close();

        } catch (IOException exc) {
            exc.printStackTrace();
        }

    }

    public void listItemClicked(View view, int position) {

        FuelLogEntry clickedItem = (FuelLogEntry) listView.getItemAtPosition(position);
        // build intent with clickedItem, then pass with the edit flag to edit activity.
        Intent intent = new Intent(MainActivity.this, editEntry.class);

        intent.putExtra(INTENT_DATA,clickedItem);
        intent.putExtra(REQUEST_CODE,EDIT_FUEL_ENTRY_OBJECT);
        intent.putExtra(FUEL_LOG_LIST_POSITION, position);

        total_amount -= clickedItem.getIntAmount();
        total_cost -= clickedItem.getIntCost();

        startActivityForResult(intent, EDIT_FUEL_ENTRY_OBJECT);
    }

    private void updateHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(total_amount / 1000);
        stringBuilder.append('.');
        stringBuilder.append((total_amount / 100) % 10);
        stringBuilder.append((total_amount / 10) % 10);
        stringBuilder.append(total_amount % 10);
        stringBuilder.append(" L");
        stringBuilder.toString();

        TextView textView = (TextView) findViewById(R.id.main_total_amount);
        textView.setText(stringBuilder.toString());

        stringBuilder = new StringBuilder();
        stringBuilder.append('$');
        stringBuilder.append(total_cost/100);
        stringBuilder.append('.');
        stringBuilder.append((total_cost / 10) % 10);
        stringBuilder.append(total_cost % 10);

        textView = (TextView) findViewById(R.id.main_total_cost);
        textView.setText(stringBuilder.toString());
    }
}
