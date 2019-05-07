package com.example.classmanageapp.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.classmanageapp.Data.ClassInfo;
import com.example.classmanageapp.DataProvider.ClassInfoDBHelper;
import com.example.classmanageapp.DataProvider.ClassInfoSharedPreferences;
import com.example.classmanageapp.MainActivity;
import com.example.classmanageapp.R;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

public class ClassRemindService extends Service {

    public static final String TAG = "启智课表";
    private int notifyId=0;//用于作为通知的id，因为每一条通知的id必需不同，所以给这个设置了递增，不用管
    private Timer timer1=new Timer();//在这个计时器中，每隔10分钟便运行一次，获取当前时间查看是否有一个小时内就要开始的课程，有的话就发送通知
    private Timer timer2=new Timer();//在这个计时器中每3小时运行一次，获取当前日期，推算出当前周数、星期，根据这个从数据库中提取当天的课程信息
    private ShareMethod sm=new ShareMethod();//这个是我写的一些跟时间操作有关系的函数，具体在sharemethod.class里面有注释
    private String[] classStartTime={"","8:00:00","10:05:00","14:00:00","16:05:00","18:40:00","20:15:00"};//记录每一节课的开始时间，方便我做转换
    private ClassInfo[] classInfos=new ClassInfo[6];//存储课程信息的对象数组，每天有6节课所以长度为6
    private int currentWeek = 1;
    private int currentSemester = 1;

    private ClassInfoDBHelper classInfoDBHelper;
    private ClassInfoSharedPreferences classInfoSharedPreferences;

    public void updateData(){
        currentWeek = classInfoSharedPreferences.getCurrentWeek();
        currentSemester = classInfoSharedPreferences.getCurrentSemester();
        classStartTime[1] = classInfoSharedPreferences.getClass1StartTime();
        classStartTime[2] = classInfoSharedPreferences.getClass2StartTime();
        classStartTime[3] = classInfoSharedPreferences.getClass3StartTime();
        classStartTime[4] = classInfoSharedPreferences.getClass4StartTime();
        classStartTime[5] = classInfoSharedPreferences.getClass5StartTime();
        classStartTime[6] = classInfoSharedPreferences.getClass6StartTime();

        classInfos = new ClassInfo[6];
        SQLiteDatabase sqLiteDatabase = classInfoDBHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Classes",
                new String[]{"name", "teacher", "place", "day", "time", "start_week", "end_week", "semester"},
                "semester = ? and start_week <= ? and end_week >= ? and day = ?",
                new String[]{String.valueOf(currentSemester), String.valueOf(currentWeek), String.valueOf(currentWeek), String.valueOf(sm.getWeekDay())},
                null, null, null);
        if(cursor.moveToFirst()){
            int i=0;
            do{
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String teacher = cursor.getString(cursor.getColumnIndex("teacher"));
                String place = cursor.getString(cursor.getColumnIndex("place"));
                int day = cursor.getInt(cursor.getColumnIndex("day"));
                int time = cursor.getInt(cursor.getColumnIndex("time"));
                int start_week = cursor.getInt(cursor.getColumnIndex("start_week"));
                int end_week = cursor.getInt(cursor.getColumnIndex("end_week"));
                int semester = cursor.getInt(cursor.getColumnIndex("semester"));
                ClassInfo classInfoTemp = new ClassInfo(name, teacher, place, day, time, semester, start_week, end_week);
                classInfos[i] = classInfoTemp;
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(ClassRemindService.this,"课程提醒服务开启",Toast.LENGTH_SHORT).show();
        classInfoDBHelper = new ClassInfoDBHelper(ClassRemindService.this, "Classes.db", null, 1);
        classInfoSharedPreferences = new ClassInfoSharedPreferences(ClassRemindService.this);
        updateData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        TimerTask task1=new TimerTask() {
            @Override
            public void run() {
                try {
                    for (int i=0;i<6;i++){
                        if (classInfos[i] != null){
                            if ( sm.timeBetween(sm.getTime(),classStartTime[classInfos[i].getTime()]) < 30  && sm.timeBetween(sm.getTime(),classStartTime[classInfos[i].getTime()]) >0){
                                classInfos[i] = null;//防止同一节课太多次重复提醒，提醒一次后就置为空

                                Intent mainIntent = new Intent(ClassRemindService.this, MainActivity.class);
                                PendingIntent mainPendingIntent = PendingIntent.getActivity(ClassRemindService.this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                Notification notifation= new Notification.Builder(ClassRemindService.this)
                                        .setContentTitle("上课啦")
                                        .setContentText(classInfos[i].getName()+"课程将于30分钟内开始")
                                        .setAutoCancel(true)
                                        .setSmallIcon(R.mipmap.icon)
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon))
                                        .setContentIntent(mainPendingIntent)
                                        .build();
                                NotificationManager manger= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                manger.notify(ClassRemindService.this.notifyId, notifation);
                                ClassRemindService.this.notifyId++;
                            }
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        TimerTask task2=new TimerTask() {
            @Override
            public void run() {
                //在后面添加代码，根据weekDay（当前是星期几）和presentWeek（当前是第几周）从数据库获取课程信息，更新classInfo
                updateData();
            }
        };

        timer1.schedule(task1,0,3000);
        timer2.schedule(task2,0,10800000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer1.cancel();
        timer2.cancel();
        Toast.makeText(ClassRemindService.this,"课程提醒服务关闭",Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
