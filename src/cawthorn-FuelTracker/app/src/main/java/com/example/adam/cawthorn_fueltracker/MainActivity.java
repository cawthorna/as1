package com.example.adam.cawthorn_fueltracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListActivity;
import java.util.ArrayList;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

    protected ArrayList<FuelLogEntry> FuelLogList = new ArrayList<FuelLogEntry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFuelLog();

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

    private void loadFuelLog() {

        FuelLogList = new ArrayList<FuelLogEntry>();
        for(int i=0; i < 20; i++) {
            FuelLogList.add(new FuelLogEntry(2000+i,2,3,"Abc",200000.2+i,"regular", 203.123, 98.0 + (((double)i)/10)));
        }

    }
}
