package com.example.ftp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private String address,password,username;
    private int port = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getSharedPreferences("connectData",MODE_PRIVATE);
        address = pref.getString("address","192.168.244.1");
        port = pref.getInt("port",21);
        username = pref.getString("username","username");
        password = pref.getString("password","password");

        Button anonymous_button = findViewById(R.id.anonymous_button);
        anonymous_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(anonymous_login()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    // TODO
                }
            }
        });

        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(login()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    // TODO
                }
            }
        });

    }

    public boolean anonymous_login(){
        boolean flag = true;
        address = findViewById(R.id.address).toString();
        //port = Integer.parseInt(findViewById(R.id.port).toString().trim());
        System.out.println("ovo");
        return flag;
    }

    public boolean login(){
        boolean flag = true;
        address = findViewById(R.id.address).toString();
        port = Integer.parseInt(findViewById(R.id.port).toString().trim());
        username = findViewById(R.id.username).toString();
        password = findViewById(R.id.password).toString();
        System.out.println("ovo");
        // TODO：连接

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
