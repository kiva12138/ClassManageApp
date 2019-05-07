package com.example.classmanageapp.DataProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ClassInfoDBHelper extends SQLiteOpenHelper {

    public static final String CREATE_CLASSES = "create table Classes(" +
            "id integer primary key autoincrement, " +
            "name text, " +
            "teacher text, " +
            "place text, " +
            "day integer, " +
            "time integer, " +
            "start_week integer, " +
            "end_week integer, " +
            "semester integer)";

    private Context mContext;

    public ClassInfoDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(this.CREATE_CLASSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Classes");
        onCreate(db);
    }

    // 添加课程
    public void addClass(String name, String teacher, String place, int day, int time, int start_week, int end_week, int semester){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("teacher", teacher);
        contentValues.put("place", place);
        contentValues.put("day", day);
        contentValues.put("time", time);
        contentValues.put("start_week", start_week);
        contentValues.put("end_week", end_week);
        contentValues.put("semester", semester);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.insert("Classes", null, contentValues);
    }

    // 删除课程
    public void deleteClass(String name, int day, int time, int semester){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("Classes", "name = ? and day = ? and time = ? and semester = ?",
                new String[]{name, String.valueOf(day), String.valueOf(time), String.valueOf(semester)});
    }

}
