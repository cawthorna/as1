package com.example.adam.cawthorn_fueltracker;

import android.app.DialogFragment;
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
import java.util.Collections;

public class MainActivity extends ActionBarActivity implements ClearFileDialogFragment.ClearFileDialogListener {

    protected final static String FUEL_LOG_STORE = "fuel_log_store";
    protected final static String INTENT_DATA = "FuelLogEntry";
    protected final static String REQUEST_CODE = "requestCode";
    protected final static String FUEL_LOG_LIST_POSITION = "fuelLogListPosition";
    static final int NEW_FUEL_ENTRY_OBJECT = 1;
    static final int EDIT_FUEL_ENTRY_OBJECT = 2;
    private static final boolean DEBUG = true;

    protected ArrayList<FuelLogEntry> FuelLogList = new ArrayList<FuelLogEntry>();
    protected ListViewArrayAdapter adapter;
    protected ListView listView;
    protected long total_cost;
    protected int total_amount;

    // Android activity lifecycle methods.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set title to make sense, not the app launcher title.
        setTitle("Fuel Tracker");

        setContentView(R.layout.activity_main);

        // Initialize ListView for showing the fuel entries, initialize the onclick listener as well.
        listView = (ListView) findViewById(R.id.listview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                listItemClicked(position);
            }
        });

    }

    // Called when the activity is started. Loads the fuel log from disk
    @Override
    public void onStart() {
        super.onStart();
        loadFuelLog();
    }

    // Called when the activity is stopped, saves the current information to disk.
    @Override
    public void onStop() {
        super.onStop();
        saveFuelLog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // if not in debug, hide the add dummy data option.
        // adapted from http://stackoverflow.com/questions/10692755/how-do-i-hide-a-menu-item-in-the-actionbar
        //noinspection PointlessBooleanExpression
        if (!DEBUG) menu.findItem(R.id.action_add_sample_data).setVisible(false);

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
            intent.putExtra(INTENT_DATA, newEntry);
            intent.putExtra(REQUEST_CODE, NEW_FUEL_ENTRY_OBJECT);

            startActivityForResult(intent, NEW_FUEL_ENTRY_OBJECT);
        }

        /* Prompt for conformation with DialogFragment, then react to the fragment result with the ClearFileDialogFragment.ClearFileDialogListener interface. */
        if(id == R.id.action_resetFile) {
            ClearFileDialogFragment dialogFragment = new ClearFileDialogFragment();
            dialogFragment.show(getFragmentManager(), "clear_entries");
        }

        /* Adds dummy data for testing and examples */
        if(id == R.id.action_add_sample_data) {
            addSampleData();
        }

        /* Sorts the entries in the log by date. */
        if(id == R.id.action_sort_entries) {
            sortFuelLogList();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* Returned from adding a new entry. Append the new entry to the FuelLogEntries ArrayList, then update the totals. */
        if(requestCode == NEW_FUEL_ENTRY_OBJECT) {
            if(resultCode == RESULT_OK) {
                FuelLogList.add((FuelLogEntry) data.getSerializableExtra(INTENT_DATA));
                total_cost += (((FuelLogEntry) data.getSerializableExtra(INTENT_DATA)).getIntCost());
                total_amount += ((FuelLogEntry) data.getSerializableExtra(INTENT_DATA)).getIntAmount();
            }
        }
        /* Returned from editing an entry. Update the edited entry at the correct position, then update the totals. */
        else if(requestCode == EDIT_FUEL_ENTRY_OBJECT) {
            if(resultCode == RESULT_OK) {
                int index = data.getIntExtra(FUEL_LOG_LIST_POSITION, -1);
                if(index >= 0) {
                    FuelLogList.set(index, (FuelLogEntry) data.getSerializableExtra(INTENT_DATA));
                    total_cost += (((FuelLogEntry) data.getSerializableExtra(INTENT_DATA)).getIntCost());
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
        // Save, update the header, and notify the ListView that the data has changed.
        updateHeader();
        saveFuelLog();
        adapter.notifyDataSetChanged();
    }

    // Interface methods for ClearFileDialogFragment
    @Override
    public void onClearFileDialogPositiveClick(DialogFragment dialogFragment) {
        File file = new File(getFilesDir(), FUEL_LOG_STORE);
        if(file.exists()) {
            if(!file.delete()) {
                System.out.println("Could not delete file \"" + FUEL_LOG_STORE + "\".");
            }
        }

        loadFuelLog();
        loadFuelLog();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, R.string.clear_entries_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClearFileDialogNegativeClick(DialogFragment dialogFragment) {
        // Do nothing, user canceled.
    }

    //Class methods
    /*
        Loads the fuel log from disk, and updates the total cost and amount.
     */
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
            bufferedReader.close();
            fileInputStream.close();

            for (FuelLogEntry entry: FuelLogList) {
                total_amount += entry.getIntAmount();
                total_cost += entry.getIntCost();
            }
            

        } catch (FileNotFoundException ex) {

            // The file does not exist. create it and return an empty list.
            // should only happen first run.
            System.out.println("File not found, creating new one. " + FUEL_LOG_STORE);
            saveFuelLog();

        } catch(IOException  ex) {
            throw new RuntimeException();

        } finally {

            updateHeader();

            adapter = new ListViewArrayAdapter(this,FuelLogList);
            listView.setAdapter(adapter);
        }
    }

    /*
        Saves the fuel log to disk.
     */
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

    /*
        Builds and starts the intent for editing a list item at the specified position.
     */
    public void listItemClicked(int position) {

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

    /*
        Builds the strings for insertion into the header for total cost and amount.
     */
    private void updateHeader() {

        Toast.makeText(MainActivity.this,"Total_cost_$: " + total_cost,Toast.LENGTH_LONG).show();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(total_amount / 1000);
        stringBuilder.append('.');
        stringBuilder.append((total_amount / 100) % 10);
        stringBuilder.append((total_amount / 10) % 10);
        stringBuilder.append(total_amount % 10);
        stringBuilder.append(" L");

        TextView textView = (TextView) findViewById(R.id.main_total_amount);
        textView.setText(stringBuilder.toString());

        stringBuilder = new StringBuilder();
        stringBuilder.append('$');
        stringBuilder.append(total_cost / (long) 100);
        stringBuilder.append('.');
        stringBuilder.append((total_cost / (long) 10)% 10);
        stringBuilder.append(total_cost % 10);

        textView = (TextView) findViewById(R.id.main_total_cost);
        textView.setText(stringBuilder.toString());
    }

    /*
        Adds sample data for testing purposes.
        Method caller is hidden when not in debug mode.
     */
    private void addSampleData() {
        for (int i = 0; i < 16; i++) {
            FuelLogList.add(new FuelLogEntry(2000 + i, 2, 3, "Abc", 200000.2 + i, "regular", 2, 100));
        }
        saveFuelLog();
        loadFuelLog();
    }

    /*
        Sorts the entries in the FuelLogList by date, then saves from disk to make it persistent
     */
    private void sortFuelLogList() {
        Collections.sort(FuelLogList);
        saveFuelLog();
        adapter.notifyDataSetChanged();
    }
}
