package com.example.classmanageapp.Data;

public class ClassInfo {
    private String name;
    private String place;
    private String teacher;
    private int day;
    private int time;
    private int semester;
    private int start_week;
    private int end_week;

    public ClassInfo(){
        this.name = "";
        this.place = "";
        this.teacher = "";
        this.day = 1;
        this.time = 1;
        this.semester = 1;
        this.start_week = 1;
        this.end_week = 1;
    }

    public ClassInfo(String name, String place, String teacher, int day, int time, int semester, int start_week, int end_week) {
        this.name = name;
        this.place = place;
        this.teacher = teacher;
        this.day = day;
        this.time = time;
        this.semester = semester;
        this.start_week = start_week;
        this.end_week = end_week;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getStart_week() {
        return start_week;
    }

    public void setStart_week(int start_week) {
        this.start_week = start_week;
    }

    public int getEnd_week() {
        return end_week;
    }

    public void setEnd_week(int end_week) {
        this.end_week = end_week;
    }
}
