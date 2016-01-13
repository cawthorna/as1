package com.example.adam.cawthorn_fueltracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListActivity;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    protected final static String FUELLOGFILE = "fuel_log_store";
    static final int GET_FUEL_ENTRY_OBJECT = 1;

    protected ArrayList<FuelLogEntry> FuelLogList = new ArrayList<FuelLogEntry>();
    protected ListViewArrayAdapter adapter;
    protected ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Fuel Tracker");

        setContentView(R.layout.activity_main);

        loadFuelLog();

        listView = (ListView) findViewById(R.id.listview);

        adapter = new ListViewArrayAdapter(this,FuelLogList);

        listView.setAdapter(adapter);

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
            Intent intent = new Intent(this, addEntry.class);
            startActivityForResult(intent, GET_FUEL_ENTRY_OBJECT);
        }

        if(id == R.id.action_refresh) {
            loadFuelLog();
            adapter.notifyDataSetChanged();
            listView.invalidateViews();
            Toast.makeText(this,"View Refreshed",Toast.LENGTH_SHORT).show();
        }

        if(id == R.id.action_resetFile) {
            File file = new File(getFilesDir(), FUELLOGFILE);
            if(file.exists()) file.delete();
            loadFuelLog();
            adapter.notifyDataSetChanged();
            listView.invalidateViews();
            Toast.makeText(this, "File rebuilt", Toast.LENGTH_SHORT).show();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this,"Not implemented yet. Maybe in the future though...",Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GET_FUEL_ENTRY_OBJECT) {
            if(resultCode == RESULT_OK) {
                FuelLogList.add((FuelLogEntry) data.getSerializableExtra("FuelLogEntry"));
            }
        }
        adapter.notifyDataSetChanged();
        listView.invalidateViews();
    }

    public class ListDisplay extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            ListViewArrayAdapter adapter = new ListViewArrayAdapter(this,FuelLogList);

            ListView listView = (ListView)  findViewById(R.id.listview);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //TODO: finish this.
                }
            });
        }
    }

    private void loadFuelLog() {
        FuelLogList.clear();

        try {
            File file = new File(getFilesDir(), FUELLOGFILE);
            if(!file.exists()) throw new FileNotFoundException();
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            try {
                while (FuelLogList.add((FuelLogEntry) inputStream.readObject())) ;
            } catch (EOFException ex) {
                //do nothing, done reading in from file.
            }
        } catch (FileNotFoundException ex) {
            // The file does not exist. create it and return an empty list.
            // should only happen first run.
            // add temporary objects for testing TODO: remove this
            System.out.println("File not found, creating new one. " + FUELLOGFILE);
            for(int i=0; i < 20; i++) {
                FuelLogList.add(new FuelLogEntry(2000+i,2,3,"Abc",200000.2+i,"regular", 203.123, 98.0 + (((double)i)/10)));
            }

            File file = new File(getFilesDir(), FUELLOGFILE);
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                ListIterator<FuelLogEntry> iterator = FuelLogList.listIterator();
                while(iterator.hasNext()) {
                    objectOutputStream.writeObject(iterator.next());
                }
                objectOutputStream.close();
                outputStream.close();

            } catch (IOException exc) {
                exc.printStackTrace();
            }

        } catch (IOException ex) {
            System.out.println("IO Exception");
            ex.printStackTrace();

        } catch (ClassNotFoundException ex) {
            System.out.println("FuelLogEntry class not found");
            ex.printStackTrace();
        }
        Collections.sort(FuelLogList);
    }
}
