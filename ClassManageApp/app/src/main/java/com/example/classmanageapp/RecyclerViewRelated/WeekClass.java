package com.example.classmanageapp.RecyclerViewRelated;

public class WeekClass {
    private String numberOfWeek;
    private String numberOfClass;

    public WeekClass(String numberOfWeek, String numberOfClass)
    {
        this.numberOfClass = numberOfClass;
        this.numberOfWeek = numberOfWeek;
    }

    public String getNumberOfWeek() {
        return numberOfWeek;
    }

    public void setNumberOfWeek(String numberOfWeek) {
        this.numberOfWeek = numberOfWeek;
    }

    public String getNumberOfClass() {
        return numberOfClass;
    }

    public void setNumberOfClass(String numberOfClass) {
        this.numberOfClass = numberOfClass;
    }
}
