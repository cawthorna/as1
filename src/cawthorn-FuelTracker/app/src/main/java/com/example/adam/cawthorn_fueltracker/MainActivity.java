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

    // Final Variables
    public static final int NEW_FUEL_ENTRY_OBJECT = 1;
    public static final int EDIT_FUEL_ENTRY_OBJECT = 2;

    protected final static String FUEL_LOG_STORE = "fuel_log_store";
    protected final static String INTENT_DATA = "FuelLogEntry";
    protected final static String REQUEST_CODE = "requestCode";
    protected final static String FUEL_LOG_LIST_POSITION = "fuelLogListPosition";

    private static final boolean DEVELOPER = false;

    // Global Variables
    protected ArrayList<FuelLogEntry> fuelLogList = new ArrayList<FuelLogEntry>();
    protected ListViewArrayAdapter adapter;
    protected ListView listView;
    protected long totalCost;
    protected int totalAmount;

    /* Android activity lifecycle methods. */

     /** Overwritten onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set title to make sense, not the app launcher title.
        setTitle("cawthorn-FuelTrack");

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

    /** Overwritten onStart method
     * Called when the activity is started. Loads the fuel log from disk
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        loadFuelLog();
    }

    /** Overwritten onStop method
     * Called when the activity is stopped, saves the current information to disk.
     *
     */
    @Override
    public void onStop() {
        super.onStop();
        saveFuelLog();
    }

    /** Overwritten onCreateOptionsMenu method
     * Creates the options menu when the user touches for the options menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // if not in debug, hide the add dummy data option.
        // adapted from http://stackoverflow.com/questions/10692755/how-do-i-hide-a-menu-item-in-the-actionbar
        //noinspection PointlessBooleanExpression
        if (!DEVELOPER) menu.findItem(R.id.action_add_sample_data).setVisible(false);

        return true;
    }

    /** Overwritten onOptionsItemSelected method
     * called when the user touches an item in the options menu.
     * @param item the item selected by the user.
     * @return
     */
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

        /* Adds dummy data for testing and examples
        *  this is hidden for production.
        */
        if(id == R.id.action_add_sample_data) {
            addSampleData();
        }

        /* Sorts the entries in the log by date. */
        if(id == R.id.action_sort_entries) {
            sortFuelLogList();
        }

        return super.onOptionsItemSelected(item);
    }

    /**Overwritten onActivityResult method
     * called when the activity called by startActivityForResult() call finishes (when the called activity is finished)
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* Returned from adding a new entry. Append the new entry to the FuelLogEntries ArrayList, then update the totals. */
        if(requestCode == NEW_FUEL_ENTRY_OBJECT) {
            if(resultCode == RESULT_OK) {
                fuelLogList.add((FuelLogEntry) data.getSerializableExtra(INTENT_DATA));
                totalCost += (((FuelLogEntry) data.getSerializableExtra(INTENT_DATA)).getLongCost());
                totalAmount += ((FuelLogEntry) data.getSerializableExtra(INTENT_DATA)).getLongAmount();
            }
        }
        /* Returned from editing an entry. Update the edited entry at the correct position, then update the totals. */
        else if(requestCode == EDIT_FUEL_ENTRY_OBJECT) {
            if(resultCode == RESULT_OK) {
                int index = data.getIntExtra(FUEL_LOG_LIST_POSITION, -1);
                if(index >= 0) {
                    fuelLogList.set(index, (FuelLogEntry) data.getSerializableExtra(INTENT_DATA));
                    totalCost += (((FuelLogEntry) data.getSerializableExtra(INTENT_DATA)).getLongCost());
                    totalAmount += ((FuelLogEntry) data.getSerializableExtra(INTENT_DATA)).getLongAmount();
                }
            } else {
                int index = data.getIntExtra(FUEL_LOG_LIST_POSITION, -1);
                if(index >= 0) {
                    FuelLogEntry temp = (FuelLogEntry) listView.getItemAtPosition(index);
                    totalAmount += temp.getLongAmount();
                    totalCost += temp.getLongCost();
                }
            }
        }
        // Save, update the header, and notify the ListView that the data has changed.
        updateHeader();
        saveFuelLog();
        adapter.notifyDataSetChanged();
    }

    // Interface methods for ClearFileDialogFragment

    /** Positive click interface method for the clear logs dialog
     *
     * @param dialogFragment
     */
    @Override
    public void onClearFileDialogPositiveClick(DialogFragment dialogFragment) {
        File file = new File(getFilesDir(), FUEL_LOG_STORE);
        if(file.exists()) {
            if(!file.delete()) {
                System.out.println("Could not delete file \"" + FUEL_LOG_STORE + "\".");
            }
        }

        //loadFuelLog();
        loadFuelLog();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, R.string.clear_entries_toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * Negative click interface method for the clear logs dialog
     * @param dialogFragment
     */
    @Override
    public void onClearFileDialogNegativeClick(DialogFragment dialogFragment) {
        // Do nothing, user canceled.
    }

    //Class methods
    /** Builds and starts the intent for editing a list item at the specified position.
     * onClick method for the ListView
     *  @param position position in the ListView of the item clicked.
     */
    public void listItemClicked(int position) {

        FuelLogEntry clickedItem = (FuelLogEntry) listView.getItemAtPosition(position);
        // build intent with clickedItem, then pass with the edit flag to edit activity.
        Intent intent = new Intent(MainActivity.this, editEntry.class);

        intent.putExtra(INTENT_DATA,clickedItem);
        intent.putExtra(REQUEST_CODE,EDIT_FUEL_ENTRY_OBJECT);
        intent.putExtra(FUEL_LOG_LIST_POSITION, position);

        totalAmount -= clickedItem.getLongAmount();
        totalCost -= clickedItem.getLongCost();

        startActivityForResult(intent, EDIT_FUEL_ENTRY_OBJECT);
    }


    /** Loads the fuel log from disk, and updates the total cost and amount.
     *
     */
    protected void loadFuelLog() {
        fuelLogList.clear();
        totalCost = 0;
        totalAmount = 0;

        try {

            FileInputStream fileInputStream = openFileInput(FUEL_LOG_STORE);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            Gson gson = new Gson();
            // below from http://google-gson.googlecode.com/svn/tags/1.1.1/docs/javadocs/com/google/gson/reflect/TypeToken.html
            fuelLogList = gson.fromJson(bufferedReader, new TypeToken<ArrayList<FuelLogEntry>>() {}.getType());
            bufferedReader.close();
            fileInputStream.close();

            for (FuelLogEntry entry: fuelLogList) {
                totalAmount += entry.getLongAmount();
                totalCost += entry.getLongCost();
            }

        } catch (FileNotFoundException ex) {

            // The file does not exist. create it and return an empty list.
            saveFuelLog();

        } catch(IOException  ex) {
            // Cant load log. remake it.
            File file = new File(getFilesDir(), FUEL_LOG_STORE);
            if(file.exists()) {
                if(!file.delete()) {
                    System.out.println("Could not delete file \"" + FUEL_LOG_STORE + "\".");
                }
            }

        } catch(RuntimeException ex) {

            // Cant load log. remake it.
            File file = new File(getFilesDir(), FUEL_LOG_STORE);
            if(file.exists()) {
                if(!file.delete()) {
                    System.out.println("Could not delete file \"" + FUEL_LOG_STORE + "\".");
                }
            }

        } finally {

            updateHeader();

            adapter = new ListViewArrayAdapter(this, fuelLogList);
            listView.setAdapter(adapter);
        }
    }

    /**
        Saves the fuel log to disk.
     */
    protected void saveFuelLog() {

        try {
            FileOutputStream fileOutputStream = openFileOutput(FUEL_LOG_STORE, MODE_PRIVATE);
            BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            Gson gson = new Gson();
            gson.toJson(fuelLogList,outputStream);
            outputStream.close();
            fileOutputStream.close();

        } catch (IOException exc) {
            exc.printStackTrace();
        }

    }

    /** Builds the strings for insertion into the header for total cost and amount.
     *
     */
    protected void updateHeader() {


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(totalAmount / 1000);
        stringBuilder.append('.');
        stringBuilder.append((totalAmount / 100) % 10);
        stringBuilder.append((totalAmount / 10) % 10);
        stringBuilder.append(totalAmount % 10);
        stringBuilder.append(" L");

        TextView textView = (TextView) findViewById(R.id.main_total_amount);
        textView.setText(stringBuilder.toString());

        stringBuilder = new StringBuilder();
        stringBuilder.append('$');
        stringBuilder.append(totalCost / (long) 100);
        stringBuilder.append('.');
        stringBuilder.append((totalCost / (long) 10)% 10);
        stringBuilder.append(totalCost % 10);

        textView = (TextView) findViewById(R.id.main_total_cost);
        textView.setText(stringBuilder.toString());
    }

    /**
     *  Adds sample data for testing purposes.
     *  Method caller is hidden from the options menu when not in developer mode.
     */
    protected void addSampleData() {
        for (int i = 0; i < 16; i++) {
            fuelLogList.add(new FuelLogEntry(2000 + i, 2, 3, "Abc", 200000.2 + i, "regular", 2, 100));
        }
        saveFuelLog();
        loadFuelLog();
    }

    /** Sorts the entries in the fuelLogList by date, then saves from disk to make it persistent
     * not required by assignment spec, but figured it would be nice to have.
     */
    protected void sortFuelLogList() {
        Collections.sort(fuelLogList);
        saveFuelLog();
        adapter.notifyDataSetChanged();
    }
}
