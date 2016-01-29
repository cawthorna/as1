package com.example.adam.cawthorn_fueltracker;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import java.util.ArrayList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ActivityInstrumentationTestCase2 {
    public ApplicationTest() {
        super(MainActivity.class);
    }

    ArrayList<FuelLogEntry> fuelLogListTest = new ArrayList<FuelLogEntry>();

    /*
     *  MainActivity Tests
     */
    public void loadFuelLogTest() {

        fuelLogListTest.clear();
        fuelLogListTest.add(new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6));

        assertEquals(fuelLogListTest.get(0).getAmount(), new testClass(fuelLogListTest).fuelLogList.get(0).getAmount());
        assertEquals(fuelLogListTest.get(0).getDate(), new testClass(fuelLogListTest).fuelLogList.get(0).getDate());
    }

    public void saveFuelLogTest() {
        loadFuelLogTest();
    }

    public void updateHeaderTest() {

        fuelLogListTest.clear();
        fuelLogListTest.add(new FuelLogEntry(2000,2,2,"station",123,"Regular",12.32,74.6));

        long cost = fuelLogListTest.get(0).getLongCost();
        long amount = fuelLogListTest.get(0).getLongAmount();

        testClass temp = new testClass(fuelLogListTest);
        temp.updateHeader();

        assertEquals(cost, temp.totalCost);
        assertEquals(amount,temp.totalAmount);


    }

    public void addSampleDataTest() {

        fuelLogListTest.clear();
        testClass temp = new testClass(fuelLogListTest);

        temp.addSampleData();

        assertFalse(fuelLogListTest.isEmpty());

    }

    public void sortFuelLogListTest() {

        fuelLogListTest.clear();
        FuelLogEntry after = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        FuelLogEntry before = new FuelLogEntry(1999, 2, 2, "station", 123, "Regular", 12.32, 74.6);

        fuelLogListTest.add(after);
        fuelLogListTest.add(before);

        testClass temp = new testClass(fuelLogListTest);

        temp.sortFuelLogList();



    }

    /*
     * editEntry Tests
     */
    public void confirmAdditionTest() {

    }

    public void cancelAdditionTest() {

    }

    public void onDateSetTest() {

    }

    /*
     * FuelLogEntry Tests
     */
    public void setDateTest() {

    }

    public void setYearTest() {

    }

    public void setMonthTest() {

    }

    public void setDayTest() {

    }

    public void setStation() {

    }

    public void setOdometerTest() {

    }

    public void setGradeTest() {

    }

    public void setAmountTest() {

    }

    public void setUnitCostTest() {

    }

    public void getDateTest() {

    }

    public void getFormattedOdometerTest() {

    }

    public void getOdometerTest() {

    }

    public void getFormattedAmountTest() {

    }

    public void getAmountTest() {

    }

    public void getFormattedUnitCostTest() {

    }

    public void getUnitCostTest() {

    }

    public void getCostTest() {

    }

    public void updateCostTest() {

    }

    public void checkCostTest() {

    }

    public void compareToTest() {

    }
}

class testClass extends MainActivity{

    public testClass(ArrayList<FuelLogEntry> fuelLogEntries){
        fuelLogList = fuelLogEntries;
        saveFuelLog();
        loadFuelLog();
    }

}
