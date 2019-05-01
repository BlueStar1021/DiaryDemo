package com.example.diarydemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassActivity extends AppCompatActivity {

    private String password = "000000";        //修改密码所需密码

    //控件集
    EditText input;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        //绑定控件
        input = (EditText)findViewById(R.id.activity_change_pass_password);
        btnOk = (Button)findViewById(R.id.activity_change_pass_btnok);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("password", MODE_PRIVATE);
                password = sharedPreferences.getString("password", "000000");

                if(input.getText().toString().equals(password) == true){
                    //密码正确

                    Toast.makeText(ChangePassActivity.this,"clicked",Toast.LENGTH_SHORT).show();

                    //弹出窗口
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePassActivity.this);
                    dialog.setTitle("请输入您的新密码:");

                    //定义一个EditText
                    final EditText newPass = new EditText(ChangePassActivity.this);

                    dialog.setView(newPass);

                    //定义确定按钮
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sharedPreferences = getSharedPreferences("password", MODE_PRIVATE);
                            SharedPreferences.Editor spe = sharedPreferences.edit();
                            spe.putString("password", newPass.getText().toString());

                            //Editor对象提交对SharedPreferences对象的更改
                            spe.commit();

                            //修改完成后返回登录界面
                            Intent intent = new Intent(ChangePassActivity.this, LoginActivity.class);
                            startActivity(intent);

                            ChangePassActivity.super.finish();
                        }
                    });

                    //定义取消按钮
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    dialog.create().show();

                }else{
                    //密码错误
                    Toast.makeText(ChangePassActivity.this, "您输入的密码不正确哟", Toast.LENGTH_SHORT).show();
                }



            }
        });




    }
}
