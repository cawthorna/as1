package com.example.adam.cawthorn_fueltracker;

import java.lang.Math;
import java.util.Calendar;

/**
 * Created by Adam on 2016-01-06.
 * Stores information on each fuel log entry
 */
public class FuelLogEntry {
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

    public boolean setDate(int year, int month, int day) {
        if(!setYear(year)) return false;
        if(!setMonth(month)) return false;
        if(!setDay(day)) return false;
        return true;
    }

    public boolean setYear(int year) {
        Calendar cal = Calendar.getInstance();
        if(year < 1000 || year > cal.get(Calendar.YEAR)) {
            return false;
        }
        this.year = year;
        return true;
    }

    public boolean setMonth(int month) {
        if(month < 1 || month > 12) {
            return false;
        }
        this.month = month;
        return true;
    }

    public boolean setDay(int day) {
        if(day < 1 || day > 31) {
            return false;
        }
        this.day = day;
        return true;
    }

    public boolean setStation(String station) {
        this.station = station;
        return true;
    }

    public boolean setOdometer(double odometer) {
        if(odometer < 0) {
            return false;
        }
        this.odometer = (int) (odometer * 10);
        return true;
    }

    public boolean setGrade(String grade) {
        this.grade = grade;
        return true;
    }

    public boolean setAmount(double amount) {
        if(amount < 0 ) {
            return false;
        }
        this.amount = (int) (amount * 1000);
        updateCost();
        return true;
    }

    public boolean setUnitCost(double unitCost) {
        if(unitCost < 0) {
            return false;
        }
        this.unitCost = (int) (unitCost * 10);
        updateCost();
        return true;
    }

    // Getter Methods

    public String getDate() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(year);
        strBuilder.append('-');
        strBuilder.append(month);
        strBuilder.append('-');
        strBuilder.append(day);
        return strBuilder.toString();
    }

    public String getStation() {
        return station;
    }

    public String getOdometer() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(odometer/10);
        stringBuilder.append('.');
        stringBuilder.append(odometer%10);
        stringBuilder.append("km");
        return stringBuilder.toString();
    }

    public String getGrade() {
        return grade;
    }

    public String getAmount() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(amount/1000);
        stringBuilder.append('.');
        stringBuilder.append((amount/100)%10);
        stringBuilder.append((amount/10)%10);
        stringBuilder.append(amount%10);
        stringBuilder.append('L');
        return stringBuilder.toString();
    }

    public String getUnitCost() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(unitCost/10);
        stringBuilder.append('.');
        stringBuilder.append(unitCost%10);
        stringBuilder.append("c/L");
        return stringBuilder.toString();
    }

    public String getCost() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('$');
        stringBuilder.append(cost/1000000);
        stringBuilder.append('.');
        stringBuilder.append((cost/100000)%10);
        stringBuilder.append((cost/10000)%10);
        return stringBuilder.toString();
    }

    public boolean updateCost() {
        this.cost = unitCost * amount;
        return true;
    }

}
