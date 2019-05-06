package com.example.classmanageapp;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 控件定义
    private ImageButton navButton;
    private ImageButton addClassButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout setWeekLinearLayout;
    private PopupWindow popupWindow;
    private RecyclerView recyclerViewWeekSet;
    private TextView currentWeekTextView;
    private ImageView currentWeekImage;
    private TextView[] classesTextViews;
    private TextView currentMonthTextView;
    private TextView class1TimeTextView;
    private TextView class2TimeTextView;
    private TextView class3TimeTextView;
    private TextView class4TimeTextView;
    private TextView class5TimeTextView;
    private TextView class6TimeTextView;
    private TextView currentSemesterHeaderTextView;
    private TextView currentWeekHeaderTextView;

    //  监听器定义
    private ClassNavViewListener classNavViewListener;
    private ClassButtonListener buttonListener;
    private ClassSingleListener classSingleListener;
    private ClassSingleLongListener classSingleLongListener;

    // Adapter
    private List<WeekClass> weekClassList = new ArrayList<>();

    // 需要的操作数据
    private String[] weeks;
    private List<ClassInfo> classInfos = new ArrayList<>();
    private int currentSemester;
    private int currentWeek;
    private String[] startTimes;
    private String[] endTimes;
    private String classNumbers;
    private int[] textViewIds;

    // SharedPreference与SQLite的访写类
    private ClassInfoDBHelper classInfoDBHelper;
    private ClassInfoSharedPreferences classInfoSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置状态栏格式，需要配合Style.xml
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        // 初始化
        initiate();
    }

    // 更新界面
    private void updateView(){
        currentSemesterHeaderTextView.setText(getResources().getString(R.string.setting_current_semester_title) + currentSemester);
        currentWeekHeaderTextView.setText(getResources().getString(R.string.setting_current_week_title) + currentWeek);
        class1TimeTextView.setText(startTimes[0] + "\n" + "-" + "\n" + endTimes[0]);
        class2TimeTextView.setText(startTimes[1] + "\n" + "-" + "\n" + endTimes[1]);
        class3TimeTextView.setText(startTimes[2] + "\n" + "-" + "\n" + endTimes[2]);
        class4TimeTextView.setText(startTimes[3] + "\n" + "-" + "\n" + endTimes[3]);
        class5TimeTextView.setText(startTimes[4] + "\n" + "-" + "\n" + endTimes[4]);
        class6TimeTextView.setText(startTimes[5] + "\n" + "-" + "\n" + endTimes[5]);

        for (int i = 0; i < 42; i++){
            classesTextViews[i].setVisibility(View.INVISIBLE);
        }
        classInfos.clear();
        SQLiteDatabase sqLiteDatabase = classInfoDBHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Classes",
                new String[]{"name", "teacher", "place", "day", "time", "start_week", "end_week", "semester"},
                "semester = ? and start_week <= ? and end_week >= ?",
                new String[]{String.valueOf(currentSemester), String.valueOf(currentWeek), String.valueOf(currentWeek)},
                null, null, null);
        if(cursor.moveToFirst()){
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
                classInfos.add(classInfoTemp);
            }while(cursor.moveToNext());
        }
        cursor.close();

        for(int i=0; i<classInfos.size(); i++){
            ClassInfo classInfo = classInfos.get(i);
            classesTextViews[(classInfo.getDay() - 1) * 6 + classInfo.getTime() - 1].setText(classInfo.getName()+"@"+classInfo.getPlace()+"-"+classInfo.getTeacher());
            classesTextViews[(classInfo.getDay() - 1) * 6 + classInfo.getTime() - 1].setVisibility(View.VISIBLE);
        }
        classNumbers = classInfoSharedPreferences.getClassNumber(this.currentSemester);
    }

    // 初始化工作
    private void initiate(){
        // 初始化监听器
        buttonListener = new ClassButtonListener();
        classNavViewListener = new ClassNavViewListener();
        classSingleListener = new ClassSingleListener();
        classSingleLongListener = new ClassSingleLongListener();

        // 初始化控件
        navButton = (ImageButton)findViewById(R.id.nav_button);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.setting_nav_view) ;
        setWeekLinearLayout = (LinearLayout) findViewById(R.id.select_week_linear_layout);
        currentWeekTextView = (TextView)findViewById(R.id.select_week_button_text);
        currentWeekImage = (ImageView)findViewById(R.id.select_week_button_image);
        addClassButton = (ImageButton)findViewById(R.id.add_class_button);
        currentMonthTextView = (TextView)findViewById(R.id.month_text);
        class1TimeTextView = (TextView)findViewById(R.id.class_time_1_text);
        class2TimeTextView = (TextView)findViewById(R.id.class_time_2_text);
        class3TimeTextView = (TextView)findViewById(R.id.class_time_3_text);
        class4TimeTextView = (TextView)findViewById(R.id.class_time_4_text);
        class5TimeTextView = (TextView)findViewById(R.id.class_time_5_text);
        class6TimeTextView = (TextView)findViewById(R.id.class_time_6_text);
        // 不能直接获取，要先获取抽屉的header，然后再寻找它的子布局
        View headerLayout = navigationView.getHeaderView(0);
        currentSemesterHeaderTextView = (TextView)headerLayout.findViewById(R.id.current_semester_text);
        currentWeekHeaderTextView = (TextView)headerLayout.findViewById(R.id.current_week_text);

        // 初始化42个课程控件
        classesTextViews = new TextView[42];
        TypedArray textViewArray = getResources().obtainTypedArray(R.array.classes_id);
        textViewIds = new int[42];
        for (int i = 0; i < 42; i++){
            textViewIds[i] = textViewArray.getResourceId(i,0);
            classesTextViews[i] = (TextView) findViewById(textViewIds[i]);
            classesTextViews[i].setOnClickListener(this.classSingleListener);
            classesTextViews[i].setOnLongClickListener(this.classSingleLongListener);
            classesTextViews[i].setVisibility(View.INVISIBLE);
        }

        // 初始化数据存储类 并初始化数据
        classInfoSharedPreferences = new ClassInfoSharedPreferences(MainActivity.this);
        classInfoDBHelper = new ClassInfoDBHelper(MainActivity.this, "Classes.db", null, 1);
        this.currentWeek = this.classInfoSharedPreferences.getCurrentWeek();
        this.currentSemester = this.classInfoSharedPreferences.getCurrentSemester();
        this.startTimes = new String[6];
        this.endTimes = new String[6];
        startTimes[0] = classInfoSharedPreferences.getClass1StartTime();
        endTimes[0] = classInfoSharedPreferences.getClass1EndTime();
        startTimes[1] = classInfoSharedPreferences.getClass2StartTime();
        endTimes[1] = classInfoSharedPreferences.getClass2EndTime();
        startTimes[2] = classInfoSharedPreferences.getClass3StartTime();
        endTimes[2] = classInfoSharedPreferences.getClass3EndTime();
        startTimes[3] = classInfoSharedPreferences.getClass4StartTime();
        endTimes[3] = classInfoSharedPreferences.getClass4EndTime();
        startTimes[4] = classInfoSharedPreferences.getClass5StartTime();
        endTimes[4] = classInfoSharedPreferences.getClass5EndTime();
        startTimes[5] = classInfoSharedPreferences.getClass6StartTime();
        endTimes[5] = classInfoSharedPreferences.getClass6EndTime();
        this.classNumbers = classInfoSharedPreferences.getClassNumber(this.currentSemester);

        // 获取资源
        weeks = getResources().getStringArray(R.array.weeks_numbers);

        // 初始化界面
        currentWeekTextView.setText(weeks[currentWeek-1]);
        updateView();

        // 设置控件监听器
        navButton.setOnClickListener(this.buttonListener);
        addClassButton.setOnClickListener(this.buttonListener);
        navigationView.setNavigationItemSelectedListener(this.classNavViewListener);
        setWeekLinearLayout.setOnClickListener(this.buttonListener);

        // 初始化当前月份 使用Calender而不是使用Date（已经弃用）
        Calendar calendar = Calendar.getInstance();
        String currentMonth = (calendar.get(Calendar.MONTH)+1) + "月";
        this.currentMonthTextView.setText(currentMonth);

    }

    // 弹出选择 如果已经弹开那么就关闭
    private void showSelectWeek(){
        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
            currentWeekImage.setImageResource(R.mipmap.arrow_drop_up_white);
            return;
        }
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.select_week_layout, null);
        popupWindow = new PopupWindow(contentView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        initiateWeekRecycler(contentView);
        popupWindow.showAsDropDown(setWeekLinearLayout);
        currentWeekImage.setImageResource(R.mipmap.arrow_drop_down_white);
    }

    // 初始化选择周数的RecyclerView
    private void initiateWeekRecycler(View parent){
        recyclerViewWeekSet = (RecyclerView)parent.findViewById(R.id.set_week_recycler);
        weekClassList.clear();
        for(int i=0; i<20; i++){
            WeekClass weekClass = new WeekClass(weeks[i], "一共" + (Integer.valueOf(classNumbers.charAt(i)) - 48) + "节课");
            weekClassList.add(weekClass);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewWeekSet.setLayoutManager(linearLayoutManager);
        WeekSelectAdapter weekSelectAdapter = new WeekSelectAdapter(weekClassList);
        recyclerViewWeekSet.setAdapter(weekSelectAdapter);
        weekSelectAdapter.notifyDataSetChanged();
        // 设置每周被选择的监听事件
        weekSelectAdapter.setOnWeekItemClickListener(new WeekSelectAdapter.OnWeekItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                currentWeekTextView.setText(weeks[(int)view.getTag()]);
                currentWeek = (int)view.getTag()+1;
                classInfoSharedPreferences.setCurrentWeek(currentWeek, MainActivity.this);
                updateView();
                popupWindow.dismiss();
                currentWeekImage.setImageResource(R.mipmap.arrow_drop_up_white);
            }
        });
    }

    // 弹出窗口添加课程
    private void addClass(){
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View view = factory.inflate(R.layout.add_class_layout, null);
        final EditText classNameTextView = (EditText)view.findViewById(R.id.add_class_input_name);
        final EditText classTeacherTextView = (EditText)view.findViewById(R.id.add_class_input_teacher);
        final EditText classPlaceTextView = (EditText)view.findViewById(R.id.add_class_input_place);
        final Spinner classSeqSpinner = (Spinner)view.findViewById(R.id.add_class_class_seq);
        final Spinner weekSeqSpinner = (Spinner)view.findViewById(R.id.add_class_class_week_seq);
        final Spinner classSemSpinner = (Spinner)view.findViewById(R.id.add_class_input_semester);
        final Spinner classStartSpinner = (Spinner)view.findViewById(R.id.add_class_input_start_week);
        final Spinner classEndSpinner = (Spinner)view.findViewById(R.id.add_class_input_end_week);
        if(classNameTextView.getText().toString().trim() ==null || classTeacherTextView.getText().toString().trim() == null
                || classPlaceTextView.getText().toString().trim() == null || classNameTextView.getText().toString().trim() ==""
                || classTeacherTextView.getText().toString().trim() == "" || classPlaceTextView.getText().toString().trim() == ""){
            Toast.makeText(MainActivity.this, "先把信息填写完整哦", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
            .setTitle("添加课程")
            .setView(view)
            .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    classInfoDBHelper.addClass(classNameTextView.getText().toString(), classTeacherTextView.getText().toString(), classPlaceTextView.getText().toString(),
                            weekSeqSpinner.getSelectedItemPosition()+1, classSeqSpinner.getSelectedItemPosition()+1,
                            classStartSpinner.getSelectedItemPosition()+1, classEndSpinner.getSelectedItemPosition() +1,
                            classSemSpinner.getSelectedItemPosition()+1);
                    classInfoSharedPreferences.addClassNumber(classSemSpinner.getSelectedItemPosition()+1,
                            classStartSpinner.getSelectedItemPosition()+1,
                            classEndSpinner.getSelectedItemPosition() +1,
                            MainActivity.this);
                    Toast.makeText(MainActivity.this, "添加"+classNameTextView.getText()+"课程成功", Toast.LENGTH_SHORT).show();
                    updateView();
                }
            })
            .setNegativeButton("取消", null)
            .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.setting_back_header_blue));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }

    // 弹出设置学期窗口
    private void setSemester(){
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View view = factory.inflate(R.layout.set_semester_layout, null);
        final Spinner semesterSpinner = (Spinner)view.findViewById(R.id.set_semester_spinner);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("设置学期")
                .setView(view)
                .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int s = semesterSpinner.getSelectedItemPosition();
                        currentSemester = s + 1;
                        classInfoSharedPreferences.setCurrentSemester(currentSemester, MainActivity.this);
                        classNumbers = classInfoSharedPreferences.getClassNumber(currentSemester);
                        updateView();
                        Toast.makeText(MainActivity.this, "设置学期成功:第"+currentSemester+"学期", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.setting_back_header_blue));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }

    // 弹出设置每节课时间窗口
    private void setClassTime(){
        View.OnClickListener timePickerListener;
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View view = factory.inflate(R.layout.set_class_time_layout, null);
        final TextView class1S = (TextView) view.findViewById(R.id.class_1_start_time_text);
        final TextView class1E = (TextView) view.findViewById(R.id.class_1_end_time_text);
        final TextView class2S = (TextView) view.findViewById(R.id.class_2_start_time_text);
        final TextView class2E = (TextView) view.findViewById(R.id.class_2_end_time_text);
        final TextView class3S = (TextView) view.findViewById(R.id.class_3_start_time_text);
        final TextView class3E = (TextView) view.findViewById(R.id.class_3_end_time_text);
        final TextView class4S = (TextView) view.findViewById(R.id.class_4_start_time_text);
        final TextView class4E = (TextView) view.findViewById(R.id.class_4_end_time_text);
        final TextView class5S = (TextView) view.findViewById(R.id.class_5_start_time_text);
        final TextView class5E = (TextView) view.findViewById(R.id.class_5_end_time_text);
        final TextView class6S = (TextView) view.findViewById(R.id.class_6_start_time_text);
        final TextView class6E = (TextView) view.findViewById(R.id.class_6_end_time_text);

        class1S.setText(startTimes[0]);
        class1E.setText(endTimes[0]);
        class2S.setText(startTimes[1]);
        class2E.setText(endTimes[1]);
        class3S.setText(startTimes[2]);
        class3E.setText(endTimes[2]);
        class4S.setText(startTimes[3]);
        class4E.setText(endTimes[3]);
        class5S.setText(startTimes[4]);
        class5E.setText(endTimes[4]);
        class6S.setText(startTimes[5]);
        class6E.setText(endTimes[5]);

        timePickerListener = new View.OnClickListener() {
            @Override
            public void onClick(final View viewText) {
                // 弹出时间选择框
                final int hour=0, min=0;
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        switch (viewText.getId()){
                            case R.id.class_1_start_time_text:
                                startTimes[0] = hourOfDay+":"+minute;
                                class1S.setText(startTimes[0]);
                                break;
                            case R.id.class_1_end_time_text:
                                endTimes[0] = hourOfDay+":"+minute;
                                class1E.setText(endTimes[0]);
                                break;
                            case R.id.class_2_start_time_text:
                                startTimes[1] = hourOfDay+":"+minute;
                                class2S.setText(startTimes[1]);
                                break;
                            case R.id.class_2_end_time_text:
                                endTimes[1] = hourOfDay+":"+minute;
                                class2E.setText(endTimes[1]);
                                break;
                            case R.id.class_3_start_time_text:
                                startTimes[2] = hourOfDay+":"+minute;
                                class3S.setText(startTimes[2]);
                                break;
                            case R.id.class_3_end_time_text:
                                endTimes[2] = hourOfDay+":"+minute;
                                class3E.setText(endTimes[2]);
                                break;
                            case R.id.class_4_start_time_text:
                                startTimes[3] = hourOfDay+":"+minute;
                                class4S.setText(startTimes[3]);
                                break;
                            case R.id.class_4_end_time_text:
                                endTimes[3] = hourOfDay+":"+minute;
                                class4E.setText(endTimes[3]);
                                break;
                            case R.id.class_5_start_time_text:
                                startTimes[4] = hourOfDay+":"+minute;
                                class5S.setText(startTimes[4]);
                                break;
                            case R.id.class_5_end_time_text:
                                endTimes[4] = hourOfDay+":"+minute;
                                class5E.setText(endTimes[4]);
                                break;
                            case R.id.class_6_start_time_text:
                                startTimes[5] = hourOfDay+":"+minute;
                                class6S.setText(startTimes[5]);
                                break;
                            case R.id.class_6_end_time_text:
                                endTimes[5] = hourOfDay+":"+minute;
                                class6E.setText(endTimes[5]);
                                break;
                        }
                    }
                }, hour, min, true).show();
            }
        };

        class1S.setOnClickListener(timePickerListener);
        class1E.setOnClickListener(timePickerListener);
        class2S.setOnClickListener(timePickerListener);
        class2E.setOnClickListener(timePickerListener);
        class3S.setOnClickListener(timePickerListener);
        class3E.setOnClickListener(timePickerListener);
        class4S.setOnClickListener(timePickerListener);
        class4E.setOnClickListener(timePickerListener);
        class5S.setOnClickListener(timePickerListener);
        class5E.setOnClickListener(timePickerListener);
        class6S.setOnClickListener(timePickerListener);
        class6E.setOnClickListener(timePickerListener);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("设置上课时间")
                .setView(view)
                .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        classInfoSharedPreferences.setClass1StartTime(startTimes[0], MainActivity.this);
                        classInfoSharedPreferences.setClass1EndTime(endTimes[0], MainActivity.this);
                        classInfoSharedPreferences.setClass2StartTime(startTimes[1], MainActivity.this);
                        classInfoSharedPreferences.setClass2EndTime(endTimes[1], MainActivity.this);
                        classInfoSharedPreferences.setClass3StartTime(startTimes[2], MainActivity.this);
                        classInfoSharedPreferences.setClass3EndTime(endTimes[2], MainActivity.this);
                        classInfoSharedPreferences.setClass4StartTime(startTimes[3], MainActivity.this);
                        classInfoSharedPreferences.setClass4EndTime(endTimes[3], MainActivity.this);
                        classInfoSharedPreferences.setClass5StartTime(startTimes[4], MainActivity.this);
                        classInfoSharedPreferences.setClass5EndTime(endTimes[4], MainActivity.this);
                        classInfoSharedPreferences.setClass6StartTime(startTimes[5], MainActivity.this);
                        classInfoSharedPreferences.setClass6EndTime(endTimes[5], MainActivity.this);
                        updateView();
                        Toast.makeText(MainActivity.this, "设置上课时间成功", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.setting_back_header_blue));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }

    // 自定义按钮点击响应事件
    private class ClassButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.nav_button:
                    if(popupWindow!=null && popupWindow.isShowing()){
                        popupWindow.dismiss();
                        currentWeekImage.setImageResource(R.mipmap.arrow_drop_up_white);
                    }
                    drawerLayout.openDrawer(GravityCompat.START);
                    break;
                case R.id.select_week_linear_layout:
                    showSelectWeek();
                    break;
                case R.id.add_class_button:
                    addClass();
                    break;
            }
        }
    }

    // 自定义导航点击响应事件
    private class ClassNavViewListener implements NavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            drawerLayout.closeDrawers();
            switch (item.getItemId()){
                case R.id.setting_set_semester:
                    setSemester();
                    break;
                case  R.id.setting_set_time:
                    setClassTime();
                    break;
            }
            return true;
        }
    }

    // 自定义每节课程点击响应事件
    private class ClassSingleListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int position = 0;
            for(int i=0; i<textViewIds.length; i++){
                if(v.getId() == textViewIds[i]){
                    position = i;
                    break;
                }
            }
            int day = position / 6 + 1;
            int time = position % 6 + 1;
            ClassInfo classInfo = new ClassInfo();
            for(int i=0; i<classInfos.size(); i++){
                if(classInfos.get(i).getDay() == day && classInfos.get(i).getTime() == time){
                    classInfo = classInfos.get(i);
                }
            }
            LayoutInflater factory = LayoutInflater.from(MainActivity.this);
            final View view = factory.inflate(R.layout.class_info_layout, null);
            final TextView classInfoName = (TextView)view.findViewById(R.id.class_info_name_text);
            final TextView classInfoPlace = (TextView)view.findViewById(R.id.class_info_place_text);
            final TextView classInfoTeacher = (TextView)view.findViewById(R.id.class_info_teacher_text);
            final TextView classInfoWeeks = (TextView)view.findViewById(R.id.class_info_start_and_end_week_text);
            final TextView classInfoSemester = (TextView)view.findViewById(R.id.class_info_semester_text);
            classInfoName.setText(classInfo.getName());
            classInfoPlace.setText(classInfo.getPlace());
            classInfoTeacher.setText(classInfo.getTeacher());
            classInfoWeeks.setText("第"+classInfo.getStart_week()+"-"+classInfo.getEnd_week()+"周");
            classInfoSemester.setText("第"+classInfo.getSemester()+"学期");
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("课程详细信息")
                    .setView(view)
                    .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.setting_back_header_blue));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            updateView();
        }
    }

    // 自定义每节课程长按响应事件
    private class ClassSingleLongListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View v) {
            LayoutInflater factory = LayoutInflater.from(MainActivity.this);
            final View view = factory.inflate(R.layout.delete_dialog_layout, null);
            int position = 0;
            for(int i=0; i<textViewIds.length; i++){
                if(v.getId() == textViewIds[i]){
                    position = i;
                    break;
                }
            }
            int day = position / 6 + 1;
            int time = position % 6 + 1;
            ClassInfo classInfo = new ClassInfo();
            for(int i=0; i<classInfos.size(); i++){
                if(classInfos.get(i).getDay() == day && classInfos.get(i).getTime() == time){
                    classInfo = classInfos.get(i);
                }
            }
            final ClassInfo finalClassInfo = classInfo;
            final ClassInfo finalClassInfo1 = classInfo;
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("确定要删除这节课程吗?")
                    .setView(view)
                    .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            classInfoDBHelper.deleteClass(finalClassInfo.getName(), finalClassInfo.getDay(), finalClassInfo.getTime(), finalClassInfo.getSemester());
                            classInfoSharedPreferences.deClassNumber(finalClassInfo1.getSemester(), finalClassInfo1.getStart_week(), finalClassInfo1.getEnd_week(), MainActivity.this);
                            Toast.makeText(MainActivity.this, "删除该课程成功", Toast.LENGTH_SHORT).show();
                            updateView();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.setting_back_header_blue));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            return true;
        }
    }

}
