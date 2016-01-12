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
import java.util.ListIterator;

import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

    protected final static String FUELLOGFILE = "fuel_log_store";

    protected ArrayList<FuelLogEntry> FuelLogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FuelLogList = loadFuelLog();

        ListView listView = (ListView) findViewById(R.id.listview);

        ListViewArrayAdapter adapter = new ListViewArrayAdapter(this,FuelLogList);

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
            startActivity(intent);

            //FuelLogList = loadFuelLog();
        }

        if(id == R.id.action_refresh) {
            FuelLogList = loadFuelLog();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ListDisplay extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            ListViewArrayAdapter adapter = new ListViewArrayAdapter(this,FuelLogList);

            ListView listView = (ListView)  findViewById(R.id.listview);
            listView.setAdapter(adapter);
        }
    }

    private ArrayList<FuelLogEntry> loadFuelLog() {
        FuelLogList = new ArrayList<FuelLogEntry>();

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
        return FuelLogList;
    }
}
