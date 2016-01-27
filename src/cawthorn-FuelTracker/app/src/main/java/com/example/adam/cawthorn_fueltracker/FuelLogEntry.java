package com.example.adam.cawthorn_fueltracker;

import java.io.Serializable;
import java.lang.Math;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Adam on 2016-01-06.
 * Stores information on each fuel log entry
 */
public class FuelLogEntry implements Serializable, Comparable<FuelLogEntry> {
    private int year, month, day;
    private String station;
    private int odometer;
    private String grade;
    private long amount;
    private int unitCost;
    private long cost;

    /**
     * Returns a new FuelLogEntry that stores information on a specific fuel up/
     * @param year int
     * @param month
     * @param day
     * @param station
     * @param odometer
     * @param grade
     * @param amount
     * @param unitCost
     * @return FuelLogEntry
     */
    public FuelLogEntry(int year, int month, int day, String station, double odometer, String grade, double amount, double unitCost) {
        setDate(year, month, day);
        setStation(station);
        setOdometer(odometer);
        setGrade(grade);
        setAmount(amount);
        setUnitCost(unitCost);
        updateCost();
    }

    // Setter Methods

    /**
     *
     * @param year
     * @param month
     * @param day
     * @return 1 on success, -1 if the year is bad, -2 if the month is bad, -3 if the day is bad, or -4 if the date is after today
     */
    public int setDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        if(date.after(cal))  return -4;
        if(!setYear(year)) return -1;
        if(!setMonth(month)) return -2;
        if(!setDay(day)) return -3;
        return 1;
    }

    /**
     * @param year
     * @return true if the year was successfully set, false otherwise.
     */
    public boolean setYear(int year) {
        Calendar cal = Calendar.getInstance();
        if(year > cal.get(Calendar.YEAR)) {
            return false;
        }
        this.year = year;
        return true;
    }

    /**
     *
     * @param month
     * @return true if the year was successfully set, false otherwise.
     */
    public boolean setMonth(int month) {
        if(month < 0 || month > 11) {
            // zero indexed month.
            return false;
        }
        this.month = month;
        return true;
    }

    /**
     *
     * @param day
     * @return true if the day was successfully set, false otherwise.
     */
    public boolean setDay(int day) {
        if(day < 0 || day > 31) {
            return false;
        }
        this.day = day;
        return true;
    }

    /**
     *
     * @param station
     * @return true if the station was successfully set, false otherwise.
     */
    public boolean setStation(String station) {
        if(station.length() <= 0) return false;
        this.station = station;
        return true;
    }

    /**
     *
     * @param odometer
     * @return true if the odometer was successfully set, false otherwise.
     */
    public boolean setOdometer(double odometer) {
        if((int) (odometer * 10) <= 0) {
            return false;
        }
        this.odometer = (int) (odometer * 10);
        return true;
    }

    /**
     *
     * @param grade
     * @return true if the grade was successfully set, false otherwise.
     */
    public boolean setGrade(String grade) {
        if(grade.length() <= 0) return false;
        this.grade = grade;
        return true;
    }

    /**
     *
     * @param amount
     * @return true if the amount was successfully set, false otherwise.
     */
    public boolean setAmount(double amount) {
        if((int) (amount * 1000) <= 0 || checkCost(unitCost,(long)amount * 1000)) {
            return false;
        }
        this.amount = (int) (amount * 1000);
        updateCost();
        return true;
    }

    /**
     *
     * @param unitCost
     * @return true if the unit cost was successfully set, false otherwise.
     */
    public boolean setUnitCost(double unitCost) {
        if((int) (unitCost * 10) <= 0 || checkCost((int)(unitCost * 1000),amount)) {
            return false;
        }
        this.unitCost = (int) (unitCost * 10);
        updateCost();
        return true;
    }

    // Getter Methods

    /**
     *
     * @return the year as an int.
     */
    public int getYear() {
        return year;
    }

    /**
     *
     * @return month as a zero based int
     */
    public int getMonth() {
        return month;
    }

    /**
     *
     * @return day of the month
     */
    public int getDay() {
        return day;
    }

    /**
     *
     * @return String based representation of the date in YYYY-MM-DD format.
     */
    public String getDate() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(year);
        strBuilder.append('-');
        strBuilder.append(month + 1);
        strBuilder.append('-');
        strBuilder.append(day);
        return strBuilder.toString();
    }

    /**
     *
     * @return Station string
     */
    public String getStation() {
        return station;
    }

    /**
     *
     * @return String of the formatted odometer to 1 decimal place with units.
     */
    public String getFormattedOdometer() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(odometer/10);
        stringBuilder.append('.');
        stringBuilder.append(odometer%10);
        stringBuilder.append(" km");
        return stringBuilder.toString();
    }

    /**
     *
     * @return String with the odometer to 1 decimal place
     */
    public String getOdometer() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(odometer/10);
        stringBuilder.append('.');
        stringBuilder.append(odometer%10);
        return stringBuilder.toString();
    }

    /**
     *
     * @return String of the grade
     */
    public String getGrade() {
        return grade;
    }

    /**
     *
     * @return String formated to 3 decimal places, with units
     */
    public String getFormattedAmount() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(amount/1000);
        stringBuilder.append('.');
        stringBuilder.append((amount/100)%10);
        stringBuilder.append((amount/10)%10);
        stringBuilder.append(amount%10);
        stringBuilder.append(" L");
        return stringBuilder.toString();
    }

    /**
     *
     * @return string formated to 3 decimal places without units.
     */
    public String getAmount() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(amount/1000);
        stringBuilder.append('.');
        stringBuilder.append((amount/100)%10);
        stringBuilder.append((amount/10)%10);
        stringBuilder.append(amount%10);
        return stringBuilder.toString();
    }

    /**
     *
     * @return amount long in Liters * 1000 for 3 decimal places.
     */
    public long getLongAmount() {
        return amount;
    }

    /**
     *
     * @return String formatted to 1 decimal place with the units.
     */
    public String getFormattedUnitCost() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(unitCost/10);
        stringBuilder.append('.');
        stringBuilder.append(unitCost%10);
        stringBuilder.append(" ¢/L");
        return stringBuilder.toString();
    }

    /**
     *
     * @return String formatted to 1 decimal place without the units.
     */
    public String getUnitCost() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(unitCost/10);
        stringBuilder.append('.');
        stringBuilder.append(unitCost%10);
        return stringBuilder.toString();
    }

    /**
     *
     * @return String formatted to 2 decimals with the units
     */
    public String getCost() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('$');
        stringBuilder.append(cost/100);
        stringBuilder.append('.');
        stringBuilder.append((cost/10)%10);
        stringBuilder.append(cost%10);
        return stringBuilder.toString();
    }

    /**
     *
     * @return long of cost * 100 (in cents)
     */
    public long getLongCost() {
        return cost;
    }

    /**
     * Updates the cost based on amount and cost per unit.
     */
    public void updateCost() {
        // (cents * 10 /L) * mL * (1/1000 L/mL) / 10
        this.cost = (((long)unitCost) * amount) / 10000;
    }

    public boolean checkCost(int unitCost, long amount) {
        return((((long)unitCost) * amount) / 10000 > Long.MAX_VALUE);
    }

    /**
     * Comparator for sorting.
     * @param fuelLogEntry
     * @return -1 if this is before the provided entry, 0 if equal, and 1 if greater.
     */
    public int compareTo(FuelLogEntry fuelLogEntry) {

        if(this.year < fuelLogEntry.getYear()) return -1;
        else if(this.year > fuelLogEntry.getYear()) return 1;
        else {
            if(this.month < fuelLogEntry.getMonth()) return -1;
            else if(this.month > fuelLogEntry.getMonth()) return 1;
            else {
                if(this.day < fuelLogEntry.getDay()) return -1;
                if(this.day > fuelLogEntry.getDay()) return 1;
                else {
                    return 0;
                }
            }
        }
    }

}
