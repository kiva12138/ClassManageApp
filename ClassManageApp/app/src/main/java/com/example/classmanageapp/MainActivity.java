package com.example.classmanageapp;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
import android.widget.Button;
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

    //  监听器定义
    private ClassNavViewListener classNavViewListener;
    private ClassButtonListener buttonListener;
    private ClassSingleListener classSingleListener;
    private ClassSingleLongListener classSingleLongListener;

    // Adapter
    private List<WeekClass> weekClassList = new ArrayList<>();

    private String[] weeks;

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

        // 初始化42个课程控件
        classesTextViews = new TextView[42];
        TypedArray textViewArray = getResources().obtainTypedArray(R.array.classes_id);
        int[] textViewIds = new int[42];
        for (int i = 0; i < 42; i++){
            textViewIds[i] = textViewArray.getResourceId(i,0);
            classesTextViews[i] = (TextView) findViewById(textViewIds[i]);
            classesTextViews[i].setOnClickListener(this.classSingleListener);
            classesTextViews[i].setOnLongClickListener(this.classSingleLongListener);
        }
        // 示例操作
        // classesTextViews[0].setText("123");
        // classesTextViews[1].setVisibility(View.INVISIBLE);

        // 获取资源
        weeks = getResources().getStringArray(R.array.weeks_numbers);

        // TODO：设置当前的周数，母目前暂且设置为1
        currentWeekTextView.setText(weeks[0]);

        // 设置控件监听器
        navButton.setOnClickListener(this.buttonListener);
        addClassButton.setOnClickListener(this.buttonListener);
        navigationView.setNavigationItemSelectedListener(this.classNavViewListener);
        setWeekLinearLayout.setOnClickListener(this.buttonListener);

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
        for(int i=0; i<20; i++){
            WeekClass weekClass = new WeekClass(weeks[i], "一共" + i + "节课");
            weekClassList.add(weekClass);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewWeekSet.setLayoutManager(linearLayoutManager);
        WeekSelectAdapter weekSelectAdapter = new WeekSelectAdapter(weekClassList);
        recyclerViewWeekSet.setAdapter(weekSelectAdapter);
        // 设置每周被选择的监听事件
        weekSelectAdapter.setOnWeekItemClickListener(new WeekSelectAdapter.OnWeekItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // view.getTag() 获取当前位置从0（周一）开始
                currentWeekTextView.setText(weeks[(int)view.getTag()]);
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
        final Spinner classSemSpinner = (Spinner)view.findViewById(R.id.add_class_input_semester);
        final Spinner classStartSpinner = (Spinner)view.findViewById(R.id.add_class_input_start_week);
        final Spinner classEndSpinner = (Spinner)view.findViewById(R.id.add_class_input_end_week);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
            .setTitle("添加课程")
            .setView(view)
            .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "添加课程成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, "设置学期成功", Toast.LENGTH_SHORT).show();
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

        timePickerListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 弹出时间选择框
                int hour=0, min=0;
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    }
                }, hour, min, true).show();

                switch (view.getId()){
                    case R.id.class_1_start_time_text:
                        break;
                    case R.id.class_1_end_time_text:
                        break;
                    case R.id.class_2_start_time_text:
                        break;
                    case R.id.class_2_end_time_text:
                        break;
                    case R.id.class_3_start_time_text:
                        break;
                    case R.id.class_3_end_time_text:
                        break;
                    case R.id.class_4_start_time_text:
                        break;
                    case R.id.class_4_end_time_text:
                        break;
                    case R.id.class_5_start_time_text:
                        break;
                    case R.id.class_5_end_time_text:
                        break;
                    case R.id.class_6_start_time_text:
                        break;
                    case R.id.class_6_end_time_text:
                        break;
                }
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
            LayoutInflater factory = LayoutInflater.from(MainActivity.this);
            final View view = factory.inflate(R.layout.class_info_layout, null);
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
        }
    }

    // 自定义每节课程长按响应事件
    private class ClassSingleLongListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View v) {
            LayoutInflater factory = LayoutInflater.from(MainActivity.this);
            final View view = factory.inflate(R.layout.delete_dialog_layout, null);
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("确定要删除这节课程吗?")
                    .setView(view)
                    .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "删除该课程成功", Toast.LENGTH_SHORT).show();
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
