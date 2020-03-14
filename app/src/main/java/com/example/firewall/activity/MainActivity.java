package com.example.firewall.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.firewall.R;

/**
 * 防火墙主界面
 * 包括手机信息、软件管家、（通讯安全）、（高级工具）、短信和电话拦截、流量统计、进程管理、病毒查杀、清除缓存、设置中心
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
