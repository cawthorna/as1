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
    private int amount;
    private int unitCost;
    private int cost;

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

    public boolean setYear(int year) {
        Calendar cal = Calendar.getInstance();
        if(year > cal.get(Calendar.YEAR)) {
            return false;
        }
        this.year = year;
        return true;
    }

    public boolean setMonth(int month) {
        if(month < 0 || month > 11) {
            // zero indexed month.
            return false;
        }
        this.month = month;
        return true;
    }

    public boolean setDay(int day) {
        if(day < 0 || day > 31) {
            return false;
        }
        this.day = day;
        return true;
    }

    public boolean setStation(String station) {
        if(station.length() <= 0) return false;
        this.station = station;
        return true;
    }

    public boolean setOdometer(double odometer) {
        if((int) (odometer * 10) <= 0) {
            return false;
        }
        this.odometer = (int) (odometer * 10);
        return true;
    }

    public boolean setGrade(String grade) {
        if(grade.length() <= 0) return false;
        this.grade = grade;
        return true;
    }

    public boolean setAmount(double amount) {
        if((int) (amount * 1000) <= 0 ) {
            return false;
        }
        this.amount = (int) (amount * 1000);
        updateCost();
        return true;
    }

    public boolean setUnitCost(double unitCost) {
        if((int) (unitCost * 10) <= 0) {
            return false;
        }
        this.unitCost = (int) (unitCost * 10);
        updateCost();
        return true;
    }

    // Getter Methods

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getDate() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(year);
        strBuilder.append('-');
        strBuilder.append(month + 1);
        strBuilder.append('-');
        strBuilder.append(day);
        return strBuilder.toString();
    }

    public String getStation() {
        return station;
    }

    public String getFormattedOdometer() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(odometer/10);
        stringBuilder.append('.');
        stringBuilder.append(odometer%10);
        stringBuilder.append(" km");
        return stringBuilder.toString();
    }

    public String getOdometer() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(odometer/10);
        stringBuilder.append('.');
        stringBuilder.append(odometer%10);
        return stringBuilder.toString();
    }

    public String getGrade() {
        return grade;
    }

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

    public String getAmount() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(amount/1000);
        stringBuilder.append('.');
        stringBuilder.append((amount/100)%10);
        stringBuilder.append((amount/10)%10);
        stringBuilder.append(amount%10);
        return stringBuilder.toString();
    }

    public int getIntAmount() {
        return amount;
    }

    public String getFormattedUnitCost() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(unitCost/10);
        stringBuilder.append('.');
        stringBuilder.append(unitCost%10);
        stringBuilder.append(" ¢/L");
        return stringBuilder.toString();
    }

    public String getUnitCost() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(unitCost/10);
        stringBuilder.append('.');
        stringBuilder.append(unitCost%10);
        return stringBuilder.toString();
    }

    public String getCost() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('$');
        stringBuilder.append(cost/100);
        stringBuilder.append('.');
        stringBuilder.append((cost/10)%10);
        stringBuilder.append(cost%10);
        return stringBuilder.toString();
    }

    public int getIntCost() {
        return cost;
    }

    public boolean updateCost() {
        this.cost = (unitCost * amount) / 10000;
        return true;
    }

    // Comparator for sorting.
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
