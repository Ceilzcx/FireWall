package com.example.firewall.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.firewall.R;

/**
 * 防火墙主界面
 * 包括手机信息、软件管家、（通讯安全）、（高级工具）、短信和电话拦截、流量统计、进程管理、病毒查杀、清除缓存、设置中心
 */
public class MainActivity extends AppCompatActivity {
    private ImageView phoneInfo;
    private ImageView communicate;
    private ImageView ProcessManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneInfo=findViewById(R.id.phone_info);
        communicate=findViewById(R.id.communication_security);
        ProcessManager=findViewById(R.id.process_management);

        phoneInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,PhoneInfoActivity.class);
                startActivity(intent);
            }
        });
        communicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CommunicateActivity.class);
                startActivity(intent);
            }
        });
        ProcessManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ProcessManagerActivity.class);
                startActivity(intent);
            }
        });

    }
}
