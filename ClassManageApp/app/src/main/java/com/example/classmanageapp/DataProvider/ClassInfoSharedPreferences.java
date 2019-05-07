package com.example.classmanageapp.DataProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ClassInfoSharedPreferences {
    private int currentSemester;
    private int currentWeek;
    private String class1StartTime;
    private String class1EndTime;
    private String class2StartTime;
    private String class2EndTime;
    private String class3StartTime;
    private String class3EndTime;
    private String class4StartTime;
    private String class4EndTime;
    private String class5StartTime;
    private String class5EndTime;
    private String class6StartTime;
    private String class6EndTime;

    private String classNumbers[];

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    public ClassInfoSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("class_info", Context.MODE_PRIVATE);

        this.currentSemester = sharedPreferences.getInt("currentSemester", 1);
        this.currentWeek = sharedPreferences.getInt("currentWeek", 1);
        this.class1StartTime = sharedPreferences.getString("class1StartTime", "8:00");
        this.class1EndTime = sharedPreferences.getString("class1EndTime", "9:30");
        this.class2StartTime = sharedPreferences.getString("class2StartTime", "10:00");
        this.class2EndTime = sharedPreferences.getString("class2EndTime", "11:30");
        this.class3StartTime = sharedPreferences.getString("class3StartTime", "14:00");
        this.class3EndTime = sharedPreferences.getString("class3EndTime", "15:30");
        this.class4StartTime = sharedPreferences.getString("class4StartTime", "16:00");
        this.class4EndTime = sharedPreferences.getString("class4EndTime", "17:30");
        this.class5StartTime = sharedPreferences.getString("class5StartTime", "18:00");
        this.class5EndTime = sharedPreferences.getString("class5EndTime", "19:30");
        this.class6StartTime = sharedPreferences.getString("class6StartTime", "20:00");
        this.class6EndTime = sharedPreferences.getString("class6EndTime", "21:30");

        classNumbers = new String[10];
        for(int i=0; i<10; i++){
            this.classNumbers[i] = sharedPreferences.getString("classNumber" + i, "00000000000000000000");
        }
    }

    public String[] getClassNumbers() {
        return classNumbers;
    }

    public String getClassNumber(int i) {
        return classNumbers[i-1];
    }

    public void setClassNumbers(String[] classNumbers, Context context) {
        this.classNumbers = classNumbers;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        for(int i=0; i<10; i++){
            editor.putString("classNumber" + i, classNumbers[i]);
        }
        editor.apply();
    }

    public void addClassNumber(int semester, int startWeek, int endWeek, Context context) {
        StringBuilder stringBuilder = new StringBuilder(classNumbers[semester-1]);
        for(int i=startWeek; i<=endWeek; i++){
            stringBuilder.replace(i-1, i, String.valueOf((char)(stringBuilder.charAt(i-1) + 1)));
        }
        classNumbers[semester - 1] = stringBuilder.toString();
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("classNumber" + semester, classNumbers[semester - 1]);
        editor.apply();
    }

    public void deClassNumber(int semester, int startWeek, int endWeek, Context context) {
        StringBuilder stringBuilder = new StringBuilder(classNumbers[semester-1]);
        for(int i=startWeek; i<=endWeek; i++){
            stringBuilder.replace(i-1, i, String.valueOf((char)(stringBuilder.charAt(i-1) - 1)));
        }
        classNumbers[semester - 1] = stringBuilder.toString();
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("classNumber" + semester, classNumbers[semester - 1]);
        editor.apply();
    }

    public int getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(int currentSemester, Context context) {
        this.currentSemester = currentSemester;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putInt("currentSemester", currentSemester);
        editor.apply();
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(int currentWeek, Context context) {
        this.currentWeek = currentWeek;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putInt("currentWeek", currentWeek);
        editor.apply();
    }

    public String getClass1StartTime() {
        return class1StartTime;
    }

    public void setClass1StartTime(String class1StartTime, Context context) {
        this.class1StartTime = class1StartTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class1StartTime", class1StartTime);
        editor.apply();
    }

    public String getClass1EndTime() {
        return class1EndTime;
    }

    public void setClass1EndTime(String class1EndTime, Context context) {
        this.class1EndTime = class1EndTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class1EndTime", class1EndTime);
        editor.apply();
    }

    public String getClass2StartTime() {
        return class2StartTime;
    }

    public void setClass2StartTime(String class2StartTime, Context context) {
        this.class2StartTime = class2StartTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class2StartTime", class2StartTime);
        editor.apply();
    }

    public String getClass2EndTime() {
        return class2EndTime;
    }

    public void setClass2EndTime(String class2EndTime, Context context) {
        this.class2EndTime = class2EndTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class2EndTime", class2EndTime);
        editor.apply();
    }

    public String getClass3StartTime() {
        return class3StartTime;
    }

    public void setClass3StartTime(String class3StartTime, Context context) {
        this.class3StartTime = class3StartTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class3StartTime", class3StartTime);
        editor.apply();
    }

    public String getClass3EndTime() {
        return class3EndTime;
    }

    public void setClass3EndTime(String class3EndTime, Context context) {
        this.class3EndTime = class3EndTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class3EndTime", class3EndTime);
        editor.apply();
    }

    public String getClass4StartTime() {
        return class4StartTime;
    }

    public void setClass4StartTime(String class4StartTime, Context context) {
        this.class4StartTime = class4StartTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class4StartTime", class4StartTime);
        editor.apply();
    }

    public String getClass4EndTime() {
        return class4EndTime;
    }

    public void setClass4EndTime(String class4EndTime, Context context) {
        this.class4EndTime = class4EndTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class4EndTime", class4EndTime);
        editor.apply();
    }

    public String getClass5StartTime() {
        return class5StartTime;
    }

    public void setClass5StartTime(String class5StartTime, Context context) {
        this.class5StartTime = class5StartTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class5StartTime", class5StartTime);
        editor.apply();
    }

    public String getClass5EndTime() {
        return class5EndTime;
    }

    public void setClass5EndTime(String class5EndTime, Context context) {
        this.class5EndTime = class5EndTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class5EndTime", class5EndTime);
        editor.apply();
    }

    public String getClass6StartTime() {
        return class6StartTime;
    }

    public void setClass6StartTime(String class6StartTime, Context context) {
        this.class6StartTime = class6StartTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class6StartTime", class6StartTime);
        editor.apply();
    }

    public String getClass6EndTime() {
        return class6EndTime;
    }

    public void setClass6EndTime(String class6EndTime, Context context) {
        this.class6EndTime = class6EndTime;
        editor = context.getSharedPreferences("class_info", context.MODE_PRIVATE).edit();
        editor.putString("class6EndTime", class6EndTime);
        editor.apply();
    }
}
