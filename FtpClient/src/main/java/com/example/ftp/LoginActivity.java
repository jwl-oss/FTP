package com.example.ftp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ftp.Utils.FtpUtil;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity{
    private String address,password,username,DTP;
    private int port = 1111;
    private EditText add_edit,port_edit,pass_edit,user_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);
        }

        add_edit = findViewById(R.id.address);
        port_edit = findViewById(R.id.port);
        user_edit = findViewById(R.id.username);
        pass_edit = findViewById(R.id.password);

        SharedPreferences pref = getSharedPreferences("connectData",MODE_PRIVATE);
        address = pref.getString("address","192.168.244.1");
        port = pref.getInt("port",21);
        username = pref.getString("username","username");
        password = pref.getString("password","password");

        Button anonymous_button = findViewById(R.id.anonymous_button);
        anonymous_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    if(anonymous_login()) {
                        Toast.makeText(v.getContext(),"连接成功",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(login()) {
                    Toast.makeText(v.getContext(),"连接成功",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    public boolean anonymous_login() throws IOException {
        boolean flag = true;
        address = add_edit.getText().toString();
        String port_string = port_edit.getText().toString().trim();
        // 填写字段不能为空
        if(address.equals("")||port_string.equals("")){
            Toast.makeText(this,"请填写完整信息",Toast.LENGTH_LONG).show();
            return false;
        }
        port = Integer.parseInt(port_string);

        // 初始化，连接
        FtpUtil.init(address,port,username,password);
        String account = "anonymous";
        if(!FtpUtil.Connect(account)){
            Toast.makeText(this,"连接失败",Toast.LENGTH_LONG).show();
            return false;
        }
        return flag;
    }

    public boolean login(){
        boolean flag = true;
        address = add_edit.getText().toString();
        String port_string = port_edit.getText().toString().trim();
        username = user_edit.getText().toString();
        password = pass_edit.getText().toString();
        // 填写字段不能为空
        if(address.equals("")||port_string.equals("")||username.equals("")||password.equals("")){
            Toast.makeText(this,"请填写完整信息",Toast.LENGTH_LONG).show();
            return false;
        }
        port = Integer.parseInt(port_string);
        // 初始化，连接
        String account = "user";
        FtpUtil.init(address,port,username,password);
        if(!FtpUtil.Connect(account)){
            Toast.makeText(this,"连接失败",Toast.LENGTH_LONG).show();
            return false;
        }
        // 信息保存
        SharedPreferences.Editor editor = getSharedPreferences("connectData",MODE_PRIVATE).edit();
        editor.putString("address",address);
        editor.putInt("port",port);
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();
        return flag;
    }
}
