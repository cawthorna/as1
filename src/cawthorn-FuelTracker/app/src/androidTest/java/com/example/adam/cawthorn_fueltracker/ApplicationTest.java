package com.example.adam.cawthorn_fueltracker;

import android.app.Application;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.IllegalFormatCodePointException;
import java.util.List;

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
    public void testLoadFuelLog() {

        fuelLogListTest.clear();
        fuelLogListTest.add(new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6));

        assertEquals(fuelLogListTest.get(0).getAmount(), new testClass(fuelLogListTest).fuelLogList.get(0).getAmount());
        assertEquals(fuelLogListTest.get(0).getDate(), new testClass(fuelLogListTest).fuelLogList.get(0).getDate());
    }

    public void testAddSampleData() {

        fuelLogListTest.clear();
        testClass temp = new testClass(fuelLogListTest);

        temp.addSampleData();

        assertFalse(fuelLogListTest.isEmpty());

    }

    public void testSortFuelLogList() {

        fuelLogListTest.clear();
        FuelLogEntry after = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        FuelLogEntry before = new FuelLogEntry(1999, 2, 2, "station", 123, "Regular", 12.32, 74.6);

        fuelLogListTest.add(after);
        fuelLogListTest.add(before);

        testClass temp = new testClass(fuelLogListTest);

        try {
            temp.sortFuelLogList();
        } catch (NullPointerException ex) {
            // Expect to be here as the adapter cannot be notified. (not available to unit tests)
            assertTrue(before.equals(temp.fuelLogList.get(0)));
        }
    }

    /*
     * FuelLogEntry Tests
     */
    public void testSetDate() {
        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        int year = 2005;
        int month = 5;
        int day = 5;
        // test good set
        fuelLogEntry.setDate(year, month, day);
        assertTrue(fuelLogEntry.getYear() == year);
        assertTrue(fuelLogEntry.getMonth() == month);
        assertTrue(fuelLogEntry.getDay() == day);

        //test bad year set.
        year = -1;
        assertEquals(-1,fuelLogEntry.setDate(year,month,day));
        year = 2005;

        //test bad month set.
        month = -1;
        assertEquals(fuelLogEntry.setDate(year,month,day),-2);
        month = 5;

        //test bad day set.
        day = -1;
        assertEquals(fuelLogEntry.setDate(year,month,day),-3);
        day = 5;

        //test after today set.
        Calendar cal = Calendar.getInstance();
        assertEquals(fuelLogEntry.setDate(cal.get(Calendar.YEAR) + 1, month, day),-4);

    }

    public void testSetYear() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        int year = 2005;

        // test good set
        fuelLogEntry.setYear(year);
        assertTrue(fuelLogEntry.getYear() == year);

        //test bad set
        assertFalse(fuelLogEntry.setYear(Calendar.getInstance().get(Calendar.YEAR)+1));
    }

    public void testSetMonth() {
        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        int month = 5;

        // test good month
        fuelLogEntry.setMonth(month);
        assertTrue(fuelLogEntry.getMonth() == month);

        //test bad months
        assertFalse(fuelLogEntry.setMonth(-1));
        assertFalse(fuelLogEntry.setMonth(12));
    }

    public void testSetDay() {
        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        int day = 5;

        // test good day
        fuelLogEntry.setDay(day);
        assertTrue(fuelLogEntry.getDay() == day);

        //test bad days
        assertFalse(fuelLogEntry.setDay(-1));
        assertFalse(fuelLogEntry.setDay(32));
    }

    public void testSetStation() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        String station = "different Station";

        fuelLogEntry.setStation(station);
        assertEquals(station, fuelLogEntry.getStation());

    }

    public void testSetOdometer() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        int odometer = 100;

        fuelLogEntry.setOdometer((double) odometer);
        assertEquals(fuelLogEntry.getOdometer(),"100.0");

    }

    public void testSetGrade() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        String grade = "ultra";

        fuelLogEntry.setGrade(grade);
        assertEquals(fuelLogEntry.getGrade(),grade);

    }

    public void testSetAmount() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        int amount = 100;

        fuelLogEntry.setAmount((double) amount);
        assertEquals(fuelLogEntry.getLongAmount(),(long)amount * 1000);

    }

    public void testSetUnitCost() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        int unitCost = 100;

        fuelLogEntry.setUnitCost((double) unitCost);
        assertEquals(fuelLogEntry.getUnitCost(),"100.0");

    }

    public void testGetDate() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        assertEquals("2000-3-2", fuelLogEntry.getDate());

    }

    public void testGetFormattedOdometer() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        assertEquals("123.0 km",fuelLogEntry.getFormattedOdometer());

    }

    public void testGetOdometer() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        assertEquals("123.0",fuelLogEntry.getOdometer());

    }

    public void testGetFormattedAmount() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        assertEquals("12.320 L",fuelLogEntry.getFormattedAmount());

    }

    public void testGetAmount() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        assertEquals("12.320",fuelLogEntry.getAmount());

    }

    public void testGetFormattedUnitCost() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        assertEquals("74.6 ¢/L",fuelLogEntry.getFormattedUnitCost());

    }

    public void testGetUnitCost() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        assertEquals("74.6",fuelLogEntry.getUnitCost());

    }

    public void testGetCost() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        assertEquals("$9.19",fuelLogEntry.getCost());

    }

    public void testUpdateCost() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        fuelLogEntry.setAmount(100);

        assertEquals("$74.60",fuelLogEntry.getCost());

    }

    public void testCheckCost() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        assertFalse(fuelLogEntry.checkCost(Integer.MAX_VALUE,Long.MAX_VALUE));

    }

    public void testCompareTo() {

        FuelLogEntry before = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        FuelLogEntry after = new FuelLogEntry(2005, 2, 2, "station", 123, "Regular", 12.32, 74.6);

        assertEquals(1,after.compareTo(before));

    }

    public void testEquals() {

        FuelLogEntry fuelLogEntry = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        FuelLogEntry fuelLogEntry1 = new FuelLogEntry(2000, 2, 2, "station", 123, "Regular", 12.32, 74.6);
        assertTrue(fuelLogEntry.equals(fuelLogEntry1));
    }
}

class testClass extends MainActivity{

    public testClass(ArrayList<FuelLogEntry> fuelLogEntries){
        fuelLogList = fuelLogEntries;
    }

    @Override
    public void saveFuelLog() {
        // do nothing, should not access disk for testing.
    }

    @Override
    public void loadFuelLog() {
        //do nothing, should not access disk for testing.
    }

}


