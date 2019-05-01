package com.example.diarydemo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    //控件集
    TextView date;
    ImageView mood;
    EditText title,content;
    Button btnRewrite,btnDelete;

    //使用DiaryDatabaseHelper操作数据库
    private DiaryDatabaseHelper dbHelper;

    //记录当前状态（0-查看态 1-修改态）
    int status = 0;

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if(status == 1){
//            final AlertDialog backAsk = new AlertDialog.Builder(DetailActivity.this).create();
//            backAsk.setIcon(R.drawable.dialog_icon);
//            backAsk.setTitle("确认返回");
//            backAsk.setMessage("您正在修改您的日记，确定要放弃修改吗？");
//            backAsk.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //关闭对话框
//                    backAsk.dismiss();
//                }
//            });
//
//            backAsk.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //关闭对话框并返回ReadActivity
//                    backAsk.dismiss();
//
//                }
//            });
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //从数据库中取出日记到数组
        dbHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        //绑定控件
        date = (TextView)findViewById(R.id.activity_detail_date);
        mood = (ImageView)findViewById(R.id.activity_detail_mood);
        title = (EditText)findViewById(R.id.activity_detail_title);
        content = (EditText)findViewById(R.id.activity_detail_content);
        btnRewrite = (Button)findViewById(R.id.activity_detail_rewrite);
        btnDelete = (Button)findViewById(R.id.activity_detail_delete);

        title.setEnabled(false);
        content.setEnabled(false);

        Intent intent = getIntent();
        final Diary diaryOnShow = new Diary(
                intent.getStringExtra("title"),
                new Date(Integer.parseInt(intent.getStringExtra("year")),
                        Integer.parseInt(intent.getStringExtra("month")),
                        Integer.parseInt(intent.getStringExtra("day"))),
                intent.getStringExtra("mood"),
                intent.getStringExtra("detail"));

        date.setText(diaryOnShow.getDate().toString());

        switch (diaryOnShow.getMood()){
            case "开心":
                mood.setImageResource(R.drawable.face_happy);
                break;
            case "愤怒":
                mood.setImageResource(R.drawable.face_angry);
                break;
            case "悲伤":
                mood.setImageResource(R.drawable.face_sad);
                break;
            case "惊恐":
                mood.setImageResource(R.drawable.face_scard);
                break;
            case "忧愁":
                mood.setImageResource(R.drawable.face_anxious);
                break;
        }

        title.setText(diaryOnShow.getTitle());

        content.setText(diaryOnShow.getDetail());



        //修改日记按钮的点击监听
        btnRewrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(status == 0){
                    //查看态下点击修改按钮：将状态转换为修改态，修改按钮的text变为保存修改，删除日记按钮变为不可点击并且变为灰色,日记内容可编辑
                    status = 1;
                    btnRewrite.setText("保存修改");
                    btnDelete.setBackgroundColor(Color.rgb(170,170,170));              //#AAAAAA
                    btnDelete.setEnabled(false);
                    title.setEnabled(true);
                    content.setEnabled(true);

                }else{
                    //修改态下点击修改按钮：将状态转换为查看态，数据库录入新的信息，修改按钮的text变为修改按钮，删除日记按钮变为可点击并且变为红色，日记内容不可编辑
                    status = 0;

                    int countOfCursor = 0;
                    Cursor cursor = db.query("diary", null, "year='" + diaryOnShow.getDate().getYear() + "' and " + "month='" + diaryOnShow.getDate().getMonth() + "' and " + "day='" + diaryOnShow.getDate().getDay() + "' and " + "title='" + title.getText().toString() + "'" , null , null , null , null);
                    if(cursor.moveToLast()){

                        do{
                            countOfCursor++;

                        }while(cursor.moveToPrevious());

                    }
                    cursor.close();

                    if(countOfCursor != 0){

                        if(diaryOnShow.getTitle().equals(title.getText().toString()) ==  true){
                            //实际没有更改title
                            ContentValues values = new ContentValues();
                            values.put("detail", content.getText().toString());
                            db.update("diary", values, "year=? and month=? and day=? and title=?", new String[]{ String.valueOf(diaryOnShow.getDate().getYear()), String.valueOf(diaryOnShow.getDate().getMonth()), String.valueOf(diaryOnShow.getDate().getDay()) , diaryOnShow.getTitle() });
                            btnRewrite.setText("修改按钮");
                            btnDelete.setBackgroundColor(Color.rgb(170,0,0));               //#AA0000
                            btnDelete.setEnabled(true);
                            title.setEnabled(false);
                            content.setEnabled(false);
                            Toast.makeText(DetailActivity.this, "您的日记 " + title.getText().toString() + " 已经保存成功。", Toast.LENGTH_SHORT).show();

                            //应用内的临时日记对象同步
                            diaryOnShow.setTitle(title.getText().toString());
                            diaryOnShow.setDetail(content.getText().toString());

                        }else{
                            //更改title导致冲突
                            Toast.makeText(DetailActivity.this, "今天已经记过一篇相同摘要的日记了哟，换一个吧~", Toast.LENGTH_SHORT).show();
                        }



                    }else{
                        ContentValues values = new ContentValues();
                        values.put("title", title.getText().toString());
                        values.put("detail", content.getText().toString());
                        db.update("diary", values, "year=? and month=? and day=? and title=?", new String[]{ String.valueOf(diaryOnShow.getDate().getYear()), String.valueOf(diaryOnShow.getDate().getMonth()), String.valueOf(diaryOnShow.getDate().getDay()) , diaryOnShow.getTitle() });

                        btnRewrite.setText("修改按钮");
                        btnDelete.setBackgroundColor(Color.rgb(170,0,0));               //#AA0000
                        btnDelete.setEnabled(true);
                        title.setEnabled(false);
                        content.setEnabled(false);
                        Toast.makeText(DetailActivity.this, "您的日记 " + title.getText().toString() + " 已经保存成功。", Toast.LENGTH_SHORT).show();

                        //应用内的临时日记对象同步
                        diaryOnShow.setTitle(title.getText().toString());
                        diaryOnShow.setDetail(content.getText().toString());
                    }




                }

            }
        });

        //删除日记按钮的点击监听
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog deleteAsk = new AlertDialog.Builder(DetailActivity.this).create();
                deleteAsk.setIcon(R.drawable.dialog_icon);
                deleteAsk.setTitle("确认删除日记");
                deleteAsk.setMessage("您将要从日记管家中永久地移除本篇日记，移除后将永远无法找回，您确定吗？");
                deleteAsk.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //关闭对话框
                        //deleteAsk.dismiss();
                    }
                });

                deleteAsk.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //关闭对话框并返回ReadActivity
                        db.delete("diary", "title=? and year=? and month=? and day=?", new String[]{ diaryOnShow.getTitle(), String.valueOf(diaryOnShow.getDate().getYear()), String.valueOf(diaryOnShow.getDate().getMonth()), String.valueOf(diaryOnShow.getDate().getDay()) });
                        Intent intent = new Intent(DetailActivity.this, ReadActivity.class);
                        startActivity(intent);
                        DetailActivity.super.finish();


                    }
                });

                deleteAsk.show();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        DetailActivity.super.finish();
        Intent intent = new Intent(DetailActivity.this, ReadActivity.class);
        startActivity(intent);
    }
}
