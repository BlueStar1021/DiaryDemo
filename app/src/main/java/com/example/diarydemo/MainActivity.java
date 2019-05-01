package com.example.diarydemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long exitTime = 0;

    //控件集
    LinearLayout write,read,recent;
    ImageView setting,alarm,music;
    RelativeLayout background;


    MediaPlayer player_spring;
    MediaPlayer player_summer;
    MediaPlayer player_autumn;
    MediaPlayer player_winter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //从SharedPreferences中获取正确的密码
        final SharedPreferences sharedPreferences = getSharedPreferences("password", MODE_PRIVATE);

        final String bgColor = sharedPreferences.getString("bgColor", "绿色");
        final SharedPreferences.Editor spe = sharedPreferences.edit();

        player_spring = new MediaPlayer().create(MainActivity.this, R.raw.spring);
        player_summer = new MediaPlayer().create(MainActivity.this, R.raw.summer);
        player_autumn = new MediaPlayer().create(MainActivity.this, R.raw.autumn);
        player_winter = new MediaPlayer().create(MainActivity.this, R.raw.winter);

        //设置音频播放器自动重复播放
        player_spring.setLooping(true);
        player_summer.setLooping(true);
        player_autumn.setLooping(true);
        player_winter.setLooping(true);

        //绑定控件
        write = (LinearLayout) findViewById(R.id.activity_main_writeblock);
        read = (LinearLayout) findViewById(R.id.activity_main_readblock);
        recent = (LinearLayout) findViewById(R.id.activity_main_recentblock);
        setting = (ImageView)findViewById(R.id.activity_main_setting);
        alarm = (ImageView)findViewById(R.id.activity_main_alarm);
        music = (ImageView)findViewById(R.id.activity_main_music);
        background = (RelativeLayout)findViewById(R.id.activity_main);

        //默认背景颜色
        switch (bgColor){
            case "绿色":
                background.setBackgroundResource(R.drawable.bgcolor_green);
                break;
            case "红色":
                background.setBackgroundResource(R.drawable.bgcolor_red);
                break;
            case "蓝色":
                background.setBackgroundResource(R.drawable.bgcolor_blue);
                break;
            case "紫色":
                background.setBackgroundResource(R.drawable.bgcolor_purple);
                break;
            default:
                background.setBackgroundResource(R.drawable.bgcolor_yellow);
        }

        //设置背景颜色按钮
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String bgColor = sharedPreferences.getString("bgColor", "绿色");
                final SharedPreferences.Editor spe = sharedPreferences.edit();

                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("设置您喜欢的背景颜色");
                builder.setIcon(R.drawable.setting_icon);

                final String[] colors = new String[]{"绿色", "红色", "蓝色", "紫色", "黄色"};

                final int[] numOfSelect = { 0 }; //记录选中位置

                switch (bgColor){
                    case "绿色":
                        numOfSelect[0] = 0;
                        break;
                    case "红色":
                        numOfSelect[0] = 1;
                        break;
                    case "蓝色":
                        numOfSelect[0] = 2;
                        break;
                    case "紫色":
                        numOfSelect[0] = 3;
                        break;
                    default:
                        numOfSelect[0] = 4;
                }

                builder.setSingleChoiceItems(colors, numOfSelect[0], new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        numOfSelect[0] = which;
                    }
                });

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        spe.putString("bgColor", colors[ numOfSelect[0] ]);
//
//                        //Editor对象提交对SharedPreferences对象的更改
//                        spe.commit();

                        String bgColor = colors[ numOfSelect[0] ];

                        switch (bgColor){
                            case "绿色":
                                background.setBackgroundResource(R.drawable.bgcolor_green);
                                spe.putString("bgColor", colors[ numOfSelect[0] ]);

                                break;
                            case "红色":
                                background.setBackgroundResource(R.drawable.bgcolor_red);
                                spe.putString("bgColor", colors[ numOfSelect[0] ]);

                                break;
                            case "蓝色":
                                background.setBackgroundResource(R.drawable.bgcolor_blue);
                                spe.putString("bgColor", colors[ numOfSelect[0] ]);

                                break;
                            case "紫色":
                                background.setBackgroundResource(R.drawable.bgcolor_purple);
                                spe.putString("bgColor", colors[ numOfSelect[0] ]);

                                break;
                            default:
                                background.setBackgroundResource(R.drawable.bgcolor_yellow);
                                spe.putString("bgColor", colors[ numOfSelect[0] ]);

                        }



                        spe.commit();
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
        });

        //设置提醒按钮
//        alarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
//                startActivity(intent);
//            }
//        });

        //设置背景音乐按钮
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("设置您喜欢的背景音乐");
                builder.setIcon(R.drawable.music_icon);

                final String[] musics = new String[]{"null", "spring", "summer", "autumn", "winter"};

                final int[] numOfSelect = { 0 }; //记录选中位置




                builder.setSingleChoiceItems(musics, numOfSelect[0], new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        numOfSelect[0] = which;
                    }
                });





                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        String bgMusic = musics[ numOfSelect[0] ];

                        switch (bgMusic){
                            case "null":
                                try{
                                    if(player_spring.isPlaying()){
                                        player_spring.pause();
                                        player_spring.seekTo(0);
                                    }

                                    if(player_summer.isPlaying()){
                                        player_summer.pause();
                                        player_summer.seekTo(0);
                                    }

                                    if(player_autumn.isPlaying()){
                                        player_autumn.pause();
                                        player_autumn.seekTo(0);
                                    }

                                    if(player_winter.isPlaying()){
                                        player_winter.pause();
                                        player_winter.seekTo(0);
                                    }

                                }catch(Exception e){
                                    Toast.makeText(MainActivity.this, "音频出错了", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case "spring":
                                try{

                                    if(player_spring.isPlaying()){

                                    }else{
                                        player_spring.start();
                                    }

                                    if(player_summer.isPlaying()){
                                        player_summer.pause();
                                        player_summer.seekTo(0);
                                    }

                                    if(player_autumn.isPlaying()){
                                        player_autumn.pause();
                                        player_autumn.seekTo(0);
                                    }

                                    if(player_winter.isPlaying()){
                                        player_winter.pause();
                                        player_winter.seekTo(0);
                                    }

                                }catch(Exception e){
                                    Toast.makeText(MainActivity.this, "音频出错了", Toast.LENGTH_SHORT).show();

                                }

                                break;
                            case "summer":
                                try{
                                    if(player_spring.isPlaying()){
                                        player_spring.pause();
                                        player_spring.seekTo(0);
                                    }

                                    if(player_summer.isPlaying()){

                                    }else{
                                        player_summer.start();
                                    }

                                    if(player_autumn.isPlaying()){
                                        player_autumn.pause();
                                        player_autumn.seekTo(0);
                                    }

                                    if(player_winter.isPlaying()){
                                        player_winter.pause();
                                        player_winter.seekTo(0);
                                    }

                                }catch(Exception e){
                                    Toast.makeText(MainActivity.this, "音频出错了", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case "autumn":
                                try{

                                    if(player_spring.isPlaying()){
                                        player_spring.pause();
                                        player_spring.seekTo(0);
                                    }

                                    if(player_summer.isPlaying()){
                                        player_summer.pause();
                                        player_summer.seekTo(0);
                                    }

                                    if(player_autumn.isPlaying()){

                                    }else{
                                        player_autumn.start();
                                    }

                                    if(player_winter.isPlaying()){
                                        player_winter.pause();
                                        player_winter.seekTo(0);
                                    }

                                }catch(Exception e){
                                    Toast.makeText(MainActivity.this, "音频出错了", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            default:
                                try{

                                    if(player_spring.isPlaying()){
                                        player_spring.pause();
                                        player_spring.seekTo(0);
                                    }

                                    if(player_summer.isPlaying()){
                                        player_summer.pause();
                                        player_summer.seekTo(0);
                                    }

                                    if(player_autumn.isPlaying()){
                                        player_autumn.pause();
                                        player_autumn.seekTo(0);
                                    }

                                    if(player_winter.isPlaying()){

                                    }else{
                                        player_winter.start();
                                    }

                                }catch(Exception e){
                                    Toast.makeText(MainActivity.this, "音频出错了", Toast.LENGTH_SHORT).show();
                                }

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
        });

        //跳转至写日记面板
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivity(intent);
            }
        });

        //跳转至看日记面板
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReadActivity.class);
                startActivity(intent);
            }
        });

        //跳转至最近的我面板
        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecentActivity.class);
                startActivity(intent);
            }
        });







    }

    //双击返回按钮退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }



}
