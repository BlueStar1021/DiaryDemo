//package com.example.diarydemo;
//
//import android.app.AlarmManager;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Build;
//import android.os.SystemClock;
//import android.provider.Settings;
//import android.support.annotation.RequiresApi;
//import android.support.v4.app.NotificationCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Switch;
//import android.widget.TimePicker;
//import android.widget.Toast;
//
//public class AlarmActivity extends AppCompatActivity {
//
//    //控件集
//    //TimerPicker timerPicker;
//    Switch orderSwitch;
//    Button btnOk;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_alarm);
//
//        //绑定控件
//        final TimePicker timerPicker = (TimePicker) findViewById(R.id.activity_alarm_picker);
//        timerPicker.setIs24HourView(true);
//        orderSwitch = (Switch)findViewById(R.id.activity_alarm_switch);
//        btnOk = (Button)findViewById(R.id.activity_alarm_btnok);
//
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onClick(View v) {
//                if(orderSwitch.isChecked()){
//                    //闹钟生效
//
//                    //Toast.makeText(AlarmActivity.this, "Set!", Toast.LENGTH_SHORT).show();
//
//
//                }else{
//                    //闹钟禁用
//                    Toast.makeText(AlarmActivity.this, "Unset!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }
//}
