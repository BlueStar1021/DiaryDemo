package com.example.diarydemo;

/**
 * Created by dell on 2019/3/31.
 */

public class Diary {

    private String title;
    private Date date;
    private String mood;
    private String detail;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = new Date(date.getYear(), date.getMonth(), date.getDay());
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Diary() {

    }

    public Diary(String title, Date date, String mood, String detail) {
        this.title = title;
        this.date = new Date(date.getYear(), date.getMonth(), date.getDay());
        this.mood = mood;
        this.detail = detail;
    }
}
