package com.example.ftp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ftp.Utils.FtpUtil;
import com.example.ftp.Utils.fileAdapter;
import com.example.ftp.Utils.transfer_fragment;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<File> fileList;
    private DrawerLayout mDrawerLayout;
    private fileAdapter adapter;
    private transfer_fragment tf;
    private String currentMode = "PASV";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tf = transfer_fragment.newInstance(currentMode);
        // 初始化页面
        // 申请权限
        getPermissions();
        initNavMenu();
    }

    // 初始化侧边栏
    private void initNavMenu(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        Button NavIcon = findViewById(R.id.setting);
        NavIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        NavigationView navigationView = findViewById(R.id.setting_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    // TODO
                    case R.id.transfer_mode:
                        if(recyclerView!=null){
                            recyclerView.setVisibility(View.GONE);
                        }
                        getSupportFragmentManager().beginTransaction().add(R.id.page,tf).commit();
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.FtpServerDirectory:
                        if(tf.isAdded())
                            getSupportFragmentManager().beginTransaction().remove(tf).commit();
                        if(recyclerView!=null){
                            recyclerView.setVisibility(View.GONE);
                        }
                        Toast.makeText(MainActivity.this,"服务器文件加载成功",Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.LocalDirectory:
                        initData();
                        Local_File();
                        if(tf.isAdded())
                            getSupportFragmentManager().beginTransaction().remove(tf).commit();
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.disconnect:
                        try {
                            FtpUtil.disconnect();
                            Toast.makeText(MainActivity.this,"断开连接",Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                }
                return true;
            }
        });
    }

    private void initData(){
        File file = Environment.getExternalStorageDirectory();
        if(file == null){
            Toast.makeText(this,"没有文件",Toast.LENGTH_SHORT).show();
            return;
        }
        fileList = new ArrayList<>(Arrays.asList(file.listFiles()));
        Toast.makeText(this,"文件加载完毕",Toast.LENGTH_SHORT).show();
    }

    private void Local_File(){
        recyclerView = (RecyclerView)findViewById(R.id.Local_files);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new fileAdapter(fileList);
        recyclerView.setAdapter(adapter);
    }

    private void getPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onBackPressed() {
        if(adapter!=null) {
            adapter.lastFile();
        }
    }
}