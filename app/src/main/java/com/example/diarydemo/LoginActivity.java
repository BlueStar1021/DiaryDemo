package com.example.diarydemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private long exitTime = 0;

    private String password = "000000";        //打开日记本所需密码

    //控件集
    EditText input;
    Button btnOk,btnChange;
    RelativeLayout screen;
    TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //绑定控件
        input = (EditText)findViewById(R.id.activity_login_password);
        btnOk = (Button)findViewById(R.id.activity_login_btnok);
        btnChange = (Button)findViewById(R.id.activity_login_btnchange);
        screen = (RelativeLayout)findViewById(R.id.activity_login);
        hint = (TextView)findViewById(R.id.activity_login_passwordhint);

        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hint.getVisibility() == View.VISIBLE){
                    Animation gone = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.gone);
                    hint.startAnimation(gone);
                    hint.setVisibility(View.GONE);
                }
            }
        });

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hint.getVisibility() == View.VISIBLE){
                    Animation gone = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.gone);
                    hint.startAnimation(gone);
                    hint.setVisibility(View.GONE);
                }
            }
        });

        //打开日记本按钮的监听事件
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //消去气泡
                if(hint.getVisibility() == View.VISIBLE){
                    Animation gone = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.gone);
                    hint.startAnimation(gone);
                    hint.setVisibility(View.GONE);
                }

                //从SharedPreferences中获取正确的密码
                SharedPreferences sharedPreferences = getSharedPreferences("password", MODE_PRIVATE);
                password = sharedPreferences.getString("password", "000000");

                //判断输入是否正确
                if(input.getText().toString().equals(password) == true){

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.super.finish();             //登录界面在本次使用中不再可见
                    Toast.makeText(LoginActivity.this, "密码正确，欢迎您", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(LoginActivity.this, "您输入的密码不正确哟", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //修改密码按钮的监听事件
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //消去气泡
                if(hint.getVisibility() == View.VISIBLE){
                    Animation gone = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.gone);
                    hint.startAnimation(gone);
                    hint.setVisibility(View.GONE);
                }

                //跳转至修改页面
                Intent intent = new Intent(LoginActivity.this, ChangePassActivity.class);
                startActivity(intent);

                //LoginActivity.super.finish();
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
