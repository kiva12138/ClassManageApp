package com.example.classmanageapp.Services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ShareMethod {

    //获取当前日期，格式为“yyyy-mm-dd”
    public String getDay(){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        StringBuffer s_month=new StringBuffer();
        StringBuffer s_day=new StringBuffer();
        s_month.append(month);
        s_day.append(day);
        if (month <10){
            s_month.insert(0,"0");
        }
        if (day <10){
            s_day.insert(0,"0");
        }
        return String.valueOf(year)+"-"+s_month.toString()+"-"+s_day.toString();
    }

    //获取当前星期，1-7
    public int getWeekDay(){
        int[] week={7, 1, 2, 3, 4, 5, 6};
        Calendar calendar=Calendar.getInstance();
        Date date=new Date(System.currentTimeMillis());
        calendar.setTime(date);
        int weekDay=calendar.get(Calendar.DAY_OF_WEEK);
        return week[weekDay-1];
    }

    //获取当前时间，格式为“hh-mm-ss”
    public String getTime(){
        Calendar c=Calendar.getInstance();
        int hourOfDay=c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int seconds=c.get(Calendar.SECOND);
        StringBuffer s_hour =new StringBuffer();
        StringBuffer s_minute=new StringBuffer();
        StringBuffer s_seconds=new StringBuffer();
        s_hour.append(hourOfDay);
        s_minute.append(minute);
        s_seconds.append(seconds);
        if(hourOfDay < 10){
            s_hour.insert(0,"0");
        }
        if (minute <10){
            s_minute.insert(0,"0");
        }
        if (seconds<10){
            s_seconds.insert(0,"0");
        }
        return s_hour.toString()+":"+s_minute.toString()+":"+s_seconds.toString();
    }

    //获取两个日期之间差距多少天，用于推算当前是第几周，参数为两个日期字符串，格式为"yyyy-MM-dd"
    public int daysBetween(String smdate,String bdate) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    //计算两个时间之间差距多少分钟，用于推算距离上课时间还有多久，参数为两个时间字符串，格式为"HH:mm:ss"
    public int timeBetween(String smdate,String bdate) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_time=(time2-time1)/(1000*60);
        return Integer.parseInt(String.valueOf(between_time));
    }

}


