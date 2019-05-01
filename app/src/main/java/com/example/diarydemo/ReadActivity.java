package com.example.diarydemo;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ReadActivity extends AppCompatActivity {

    //用于控制“到底”提示短时间内只显示一次
    private long toastTime = 0;

    //控件集
    ListView list;
    Button btnDate, btnMood, btnRecent;

    //使用DiaryDatabaseHelper操作数据库
    private DiaryDatabaseHelper dbHelper;

    ArrayList<Diary> diaryList = new ArrayList<>();     //定义ArrayList用于存放来自数据库的日记数据

    //记录默认浏览时的当前显示记录条数（初始显示20条，每次添加20条）
    final int[] queryedNum = { 0 };
    int queryEveryTime = 20;
    int extraGettingTimes = 0;

    //根据系统日期默认设置日记的日期
    Calendar calendar = Calendar.getInstance();
    public int system_year = calendar.get(Calendar.YEAR);
    public int system_month = calendar.get(Calendar.MONTH) + 1;        //在Calendar中封装的月份是以0为首月的，因此要+1
    public int system_day = calendar.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

    }


    @Override
    protected void onStart() {
        super.onStart();

        //每次刷新数据前都要将之前的数据清空
        diaryList.clear();

        //绑定控件
        list = (ListView)findViewById(R.id.activity_read_list);
        btnDate = (Button)findViewById(R.id.activity_read_selectbtn_date);
        btnMood = (Button)findViewById(R.id.activity_read_selectbtn_mood);
        btnRecent = (Button)findViewById(R.id.activity_read_selectbtn_recent);


        //从数据库中取出日记到数组
        dbHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor_count = db.query("diary", null, null, null, null, null, null);


        //获取记录数量
        int countOfCursor = 0;

        if(cursor_count.moveToLast()){

            do{
                countOfCursor++;

            }while(cursor_count.moveToPrevious());

        }
        cursor_count.close();


        //取出默认记录

        if(countOfCursor >= queryEveryTime){
            //数量足够一页，分页取
            Cursor cursor = db.query("diary",null,null,null,null,null,null, String.valueOf( countOfCursor - queryEveryTime ) + "," + String.valueOf( queryEveryTime ) );

            if(cursor.moveToLast()){

                do{
                    Diary newDiary = new Diary();
                    newDiary.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    newDiary.setDate(new Date(cursor.getInt(cursor.getColumnIndex("year")), cursor.getInt(cursor.getColumnIndex("month")), cursor.getInt(cursor.getColumnIndex("day"))));
                    newDiary.setMood(cursor.getString(cursor.getColumnIndex("mood")));
                    newDiary.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
                    diaryList.add(newDiary);
                }while(cursor.moveToPrevious());

            }
            cursor.close();
            queryedNum[0] = queryEveryTime;

        }else{
            //数量不足，一次性全部取出
            Cursor cursor = db.query("diary",null,null,null,null,null,null,null );

            if(cursor.moveToLast()){

                do{
                    Diary newDiary = new Diary();
                    newDiary.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    newDiary.setDate(new Date(cursor.getInt(cursor.getColumnIndex("year")), cursor.getInt(cursor.getColumnIndex("month")), cursor.getInt(cursor.getColumnIndex("day"))));
                    newDiary.setMood(cursor.getString(cursor.getColumnIndex("mood")));
                    newDiary.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
                    diaryList.add(newDiary);
                }while(cursor.moveToPrevious());

            }
            cursor.close();
            queryedNum[0] = countOfCursor;
        }



        //为列表定义适配器
        DiaryAdapter adapter = new DiaryAdapter(ReadActivity.this, R.layout.diary_item, diaryList);

        list.setAdapter(adapter);



        //按日期查找日记
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //呼出日期选择窗口
                createDateDialog(db);

            }
        });

        //按心情查找日记
        btnMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //呼出心情选择窗口
                createMoodDialog(db);

            }
        });

        //按心情查找日记
        btnRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //呼出心情选择窗口
                createRecentDialog(db);

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Diary diaryBeClicked = diaryList.get(position);
                //跳转事宜
                Intent intent = new Intent(ReadActivity.this, DetailActivity.class);
                intent.putExtra("title", diaryBeClicked.getTitle());
                intent.putExtra("year", String.valueOf(diaryBeClicked.getDate().getYear()));
                intent.putExtra("month", String.valueOf(diaryBeClicked.getDate().getMonth()));
                intent.putExtra("day", String.valueOf(diaryBeClicked.getDate().getDay()));
                intent.putExtra("mood", diaryBeClicked.getMood());
                intent.putExtra("detail", diaryBeClicked.getDetail());
                startActivity(intent);

                ReadActivity.super.finish();

            }
        });

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem + visibleItemCount >= totalItemCount){


                    //获取记录数量
                    Cursor cursor_count = db.query("diary", null, null, null, null, null, null);
                    int countOfCursor = 0;

                    if(cursor_count.moveToLast()){

                        do{
                            countOfCursor++;

                        }while(cursor_count.moveToPrevious());

                    }
                    cursor_count.close();

                    if(countOfCursor - queryedNum[0] > 0){
                        //有数据，追加


                        if(countOfCursor - queryedNum[0] >= queryEveryTime){
                            //满追加
                            queryedNum[0] += queryEveryTime;                //显示数据指示数加20
                            final Cursor cursor = db.query("diary",null,null,null,null,null,null, String.valueOf( countOfCursor - queryedNum[0] ) + "," + String.valueOf( queryEveryTime ) );

                            if(cursor.moveToLast()){

                                do{
                                    Diary newDiary = new Diary();
                                    newDiary.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                                    newDiary.setDate(new Date(cursor.getInt(cursor.getColumnIndex("year")), cursor.getInt(cursor.getColumnIndex("month")), cursor.getInt(cursor.getColumnIndex("day"))));
                                    newDiary.setMood(cursor.getString(cursor.getColumnIndex("mood")));
                                    newDiary.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
                                    diaryList.add(newDiary);
                                }while(cursor.moveToPrevious());

                            }
                            cursor.close();
                            DiaryAdapter adapter = new DiaryAdapter(ReadActivity.this, R.layout.diary_item, diaryList);
                            list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            list.setSelection(firstVisibleItem);

                        }else{
                            //不满追加
                            int numOfThisQuery = countOfCursor - queryedNum[0];                 //记录新加入条数
                            queryedNum[0] = countOfCursor;
                            final Cursor cursor = db.query("diary",null,null,null,null,null,null, String.valueOf( countOfCursor - queryedNum[0] ) + "," + String.valueOf( numOfThisQuery ) );

                            if(cursor.moveToLast()){

                                do{
                                    Diary newDiary = new Diary();
                                    newDiary.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                                    newDiary.setDate(new Date(cursor.getInt(cursor.getColumnIndex("year")), cursor.getInt(cursor.getColumnIndex("month")), cursor.getInt(cursor.getColumnIndex("day"))));
                                    newDiary.setMood(cursor.getString(cursor.getColumnIndex("mood")));
                                    newDiary.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
                                    diaryList.add(newDiary);
                                }while(cursor.moveToPrevious());

                            }
                            cursor.close();
                            DiaryAdapter adapter = new DiaryAdapter(ReadActivity.this, R.layout.diary_item, diaryList);
                            list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            list.setSelection(firstVisibleItem);

                        }


                    }else{
                        //无数据,不追加

                        if ((System.currentTimeMillis() - toastTime) > 2000) {
                            Toast.makeText(ReadActivity.this, "已显示全部日记", Toast.LENGTH_SHORT).show();
                            toastTime = System.currentTimeMillis();
                        } else {

                        }

                    }

                }
            }
        });

        //列表适配器重新加载数据
        adapter.notifyDataSetChanged();
    }

    public void createDateDialog(final SQLiteDatabase db){
        DatePickerDialog datePickerDialog = new DatePickerDialog(ReadActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date date = new Date(year,month + 1,dayOfMonth);

                Cursor cursor = db.query("diary", null, "year='" + date.getYear() + "' and " + "month='" + date.getMonth() + "' and " + "day='" + date.getDay() + "'", null, null, null, null);
                readDataFromCursor(cursor, list);

            }
        }, system_year, system_month - 1, system_day);

        datePickerDialog.show();
    }

    public void createMoodDialog(final SQLiteDatabase db){


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("按心情查找日记");
        builder.setIcon(R.drawable.dialog_icon);

        final String[] moods = new String[]{"开心", "愤怒", "悲伤", "惊恐", "忧愁"};

        final int[] numOfSelect = { 0 }; //记录选中位置

        builder.setSingleChoiceItems(moods, 0, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                numOfSelect[0] = which;

            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cursor cursor = db.query("diary", null, "mood='" + moods[ numOfSelect[0] ] + "'", null, null, null, null);
                readDataFromCursor(cursor, list);
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    public void createRecentDialog(final SQLiteDatabase db){

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("查找最近的日记");
        builder.setIcon(R.drawable.dialog_icon);

        final String[] recents = new String[]{"今天", "近一周", "近一月", "更早"};

        final int[] numOfSelect = { 0 }; //记录选中位置

        builder.setSingleChoiceItems(recents, 0, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                numOfSelect[0] = which;
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(numOfSelect[0] == 0){
                    //今天
                    Cursor cursor = db.query("diary", null, "year='" + system_year + "' and " + "month='" + system_month + "' and " + "day='" + system_day + "'", null, null, null, null);
                    readDataFromCursor(cursor, list);

                }else if(numOfSelect[0] == 1){
                    //近一周
                    diaryList.clear();      //清空上次的数据

                    int year = system_year;
                    int month = system_month;
                    int day = system_day;
                    //Date date = new Date(year, month, day);

                    for(int i = 0 ; i < 7 + 1 ; i++){       //往前推7天

                        Cursor cursor = db.query("diary", null, "year='" + year + "' and " + "month='" + month + "' and " +
                                "day='" + day + "'", null, null, null, null);

                        if(cursor.moveToLast()){

                            do{
                                Diary newDiary = new Diary();
                                newDiary.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                                newDiary.setDate(new Date(cursor.getInt(cursor.getColumnIndex("year")), cursor.getInt(cursor.getColumnIndex("month")), cursor.getInt(cursor.getColumnIndex("day"))));
                                newDiary.setMood(cursor.getString(cursor.getColumnIndex("mood")));
                                newDiary.setDetail(cursor.getString(cursor.getColumnIndex("detail")));

                                diaryList.add(newDiary);

                            }while(cursor.moveToPrevious());

                        }
                        cursor.close();




                        /* 日期递变规律 */
                        if(day == 1){
                            switch(month - 1){
                                case Calendar.JANUARY:
                                    year--;
                                    month = 12;
                                    day = 31;
                                    break;
                                case Calendar.FEBRUARY:
                                    month--;
                                    day = 31;
                                    break;
                                case  Calendar.MARCH:
                                    month--;
                                    if(year % 4 == 0 && year % 100 == 0 && year % 400 != 0){
                                        //闰年
                                        day = 29;
                                    }else{
                                        //平年
                                        day = 28;
                                    }
                                    break;
                                case Calendar.APRIL:
                                    month--;
                                    day = 31;
                                    break;
                                case Calendar.MAY:
                                    month--;
                                    day = 30;
                                    break;
                                case Calendar.JUNE:
                                    month--;
                                    day = 31;
                                    break;
                                case Calendar.JULY:
                                    month--;
                                    day = 30;
                                    break;
                                case  Calendar.AUGUST:
                                    month--;
                                    day = 31;
                                    break;
                                case Calendar.SEPTEMBER:
                                    month--;
                                    day = 31;
                                    break;
                                case Calendar.OCTOBER:
                                    month--;
                                    day = 30;
                                    break;
                                case Calendar.NOVEMBER:
                                    month--;
                                    day = 31;
                                    break;
                                case Calendar.DECEMBER:
                                    month--;
                                    day = 30;
                                    break;
                            }


                        }else {
                            day--;
                        }
                        /* 日期递变规律 */



                    }

                    DiaryAdapter adapter = new DiaryAdapter(ReadActivity.this, R.layout.diary_item, diaryList);
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else if(numOfSelect[0] == 2){
                    //近一月
                    diaryList.clear();      //清空上次的数据

                    int year = system_year;
                    int month = system_month;
                    int day = system_day;
                    //Date date = new Date(year, month, day);

                    for(int i = 0 ; i < 30 + 1 ; i++){       //往前推30天

                        Cursor cursor = db.query("diary", null, "year='" + year + "' and " + "month='" + month + "' and " +
                                "day='" + day + "'", null, null, null, null);

                        if(cursor.moveToLast()){

                            do{
                                Diary newDiary = new Diary();
                                newDiary.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                                newDiary.setDate(new Date(cursor.getInt(cursor.getColumnIndex("year")), cursor.getInt(cursor.getColumnIndex("month")), cursor.getInt(cursor.getColumnIndex("day"))));
                                newDiary.setMood(cursor.getString(cursor.getColumnIndex("mood")));
                                newDiary.setDetail(cursor.getString(cursor.getColumnIndex("detail")));

                                diaryList.add(newDiary);

                            }while(cursor.moveToPrevious());

                        }
                        cursor.close();

                        /* 日期递变规律 */
                        if(day == 1){
                            switch(month - 1){
                                case Calendar.JANUARY:
                                    year--;
                                    month = 12;
                                    day = 31;
                                    break;
                                case Calendar.FEBRUARY:
                                    month--;
                                    day = 31;
                                    break;
                                case  Calendar.MARCH:
                                    month--;
                                    if(year % 4 == 0 && year % 100 == 0 && year % 400 != 0){
                                        //闰年
                                        day = 29;
                                    }else{
                                        //平年
                                        day = 28;
                                    }
                                    break;
                                case Calendar.APRIL:
                                    month--;
                                    day = 31;
                                    break;
                                case Calendar.MAY:
                                    month--;
                                    day = 30;
                                    break;
                                case Calendar.JUNE:
                                    month--;
                                    day = 31;
                                    break;
                                case Calendar.JULY:
                                    month--;
                                    day = 30;
                                    break;
                                case  Calendar.AUGUST:
                                    month--;
                                    day = 31;
                                    break;
                                case Calendar.SEPTEMBER:
                                    month--;
                                    day = 31;
                                    break;
                                case Calendar.OCTOBER:
                                    month--;
                                    day = 30;
                                    break;
                                case Calendar.NOVEMBER:
                                    month--;
                                    day = 31;
                                    break;
                                case Calendar.DECEMBER:
                                    month--;
                                    day = 30;
                                    break;
                            }

                        }else {
                            day--;
                        }
                        /* 日期递变规律 */



                    }

                    DiaryAdapter adapter = new DiaryAdapter(ReadActivity.this, R.layout.diary_item, diaryList);
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else{
                    //更早 - 全数据加载
                    Cursor cursor = db.query("diary", null, null, null, null, null, null);
                    readDataFromCursor(cursor,list);

                }


                dialog.dismiss();

            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    public void readDataFromCursor(Cursor cursor, ListView list){
        diaryList.clear();

        if(cursor.moveToLast()){

            do{
                Diary newDiary = new Diary();
                newDiary.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                newDiary.setDate(new Date(cursor.getInt(cursor.getColumnIndex("year")), cursor.getInt(cursor.getColumnIndex("month")), cursor.getInt(cursor.getColumnIndex("day"))));
                newDiary.setMood(cursor.getString(cursor.getColumnIndex("mood")));
                newDiary.setDetail(cursor.getString(cursor.getColumnIndex("detail")));

                this.diaryList.add(newDiary);

            }while(cursor.moveToPrevious());

        }
        cursor.close();

        DiaryAdapter adapter = new DiaryAdapter(ReadActivity.this, R.layout.diary_item, diaryList);

        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }


}
