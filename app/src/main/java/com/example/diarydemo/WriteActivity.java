package com.example.diarydemo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class WriteActivity extends AppCompatActivity {

    //控件集
    EditText year,month,day;
    EditText title,detail;
    RadioGroup mood;
    Button save;

    //使用DiaryDatabaseHelper操作数据库
    private DiaryDatabaseHelper dbHelper;

    //当前日记对象
    Diary diary = new Diary();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        //绑定控件
        year = (EditText)findViewById(R.id.activity_write_year);
        month = (EditText)findViewById(R.id.activity_write_month);
        day = (EditText)findViewById(R.id.activity_write_day);

        title = (EditText)findViewById(R.id.activity_write_title_content);
        mood = (RadioGroup)findViewById(R.id.activity_write_moodgroup);

        diary.setMood("开心");        //心情默认被选中的是“开心”，因此这里需要默认制定为"开心"

        mood.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton)findViewById(checkedId);

                //为日记设置心情
                diary.setMood(rb.getText().toString());
            }
        });

        detail = (EditText)findViewById(R.id.activity_write_detail_content);
        save = (Button)findViewById(R.id.activity_write_save);


        //根据系统日期默认设置日记的日期
        Calendar calendar = Calendar.getInstance();
        final int system_year = calendar.get(Calendar.YEAR);
        final int system_month = calendar.get(Calendar.MONTH) + 1;        //在Calendar中封装的月份是以0为首月的，因此要+1
        final int system_day = calendar.get(Calendar.DAY_OF_MONTH);


        year.setText(String.valueOf(system_year) + "年");
        month.setText(String.valueOf(system_month) + "月");
        day.setText(String.valueOf(system_day) + "日");

        //dbHelper建立数据库文件(通过版本号保证只建立一次)
        dbHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        //点击保存日记按钮的监听事件
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判定后续工作是否继续（0-继续，1-跳过）
                int judge = 0;

                try{
                    int year_num = Integer.parseInt(String.valueOf(system_year));
                    int month_num = Integer.parseInt(String.valueOf(system_month));
                    int day_num = Integer.parseInt(String.valueOf(system_day));

                    //为日记设置日期
                    diary.setDate(new Date(year_num,month_num,day_num));
                }catch (Exception e){
                    judge = 1;
                    Toast.makeText(WriteActivity.this, "日期不正确哟", Toast.LENGTH_SHORT).show();
                }

                if(judge == 0){

                    //判断摘要和内容是否为空字符串
                    if(title.getText().toString().equals("") != true && detail.getText().toString().equals("") != true){
                        //均不为空

                        //为日记设置摘要
                        diary.setTitle(title.getText().toString());
                        //为日记设置内容
                        diary.setDetail(detail.getText().toString());

                        int exist = 0;          //用来判断数据库中摘要和日期相同的日记数

                        Cursor cursor = db.query("diary", null, "title='" + diary.getTitle() + "' and " + "year='" + diary.getDate().getYear() + "' and " + "month='" + diary.getDate().getMonth() + "' and " + "day='" + diary.getDate().getDay() + "'", null, null, null, null);
                        exist = cursor.getCount();
                        cursor.close();

                        if(exist != 0){
                            //存在相同
                            Toast.makeText(WriteActivity.this, "相同摘要的日记您今天已经记过一篇了哟,不如换个标题吧", Toast.LENGTH_SHORT).show();
                        }else{
                            //不存在相同

                            //将日记装入数据库
                            ContentValues values = new ContentValues();
                            values.put("title", diary.getTitle());
                            values.put("year", diary.getDate().getYear());
                            values.put("month", diary.getDate().getMonth());
                            values.put("day", diary.getDate().getDay());
                            values.put("mood", diary.getMood());
                            values.put("detail", diary.getDetail());
                            db.insert("diary", null, values);

                            //跳转至MainActivity并告知用户保存成功
                            //Intent intent = new Intent(WriteActivity.this, MainActivity.class);
                            //startActivity(intent);
                            Toast.makeText(WriteActivity.this, "您的日记 " + diary.getTitle() + " 已经保存成功，您可以在【看日记】中找到它", Toast.LENGTH_SHORT).show();
                            WriteActivity.super.finish();
                        }



                    }else{
                        Toast.makeText(WriteActivity.this, "日记的摘要和内容不能为空哟", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }
}
