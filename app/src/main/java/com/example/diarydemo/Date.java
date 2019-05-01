package com.example.diarydemo;

/**
 * Created by dell on 2019/3/31.
 */

public class Date {

    private int year;
    private int month;
    private int day;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Date() {

    }

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    //以长字符串形式输出日期
    public String toString(){
        return this.getYear() + "-" + this.getMonth() + "-" + this.getDay();
    }

}
