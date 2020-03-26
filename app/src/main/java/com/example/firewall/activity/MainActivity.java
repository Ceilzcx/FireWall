package com.example.firewall.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.firewall.R;
import com.example.firewall.service.BlacklistInterceptService;
import com.example.firewall.util.PermissionsUtils;

/**
 * 防火墙主界面
 * 包括手机信息、软件管家、（通讯安全）、（高级工具）、短信和电话拦截、流量统计、进程管理、病毒查杀、清除缓存、设置中心
 */
public class MainActivity extends AppCompatActivity {
    private ImageView phoneInfo;
    private ImageView communicate;
    private ImageView processManager;
    private ImageView virusKilling;
    private ImageView softManager;
    private ImageView trafficManager;
    private ImageView cleanManager;
    private ImageView wifiManager;
    private ImageView portManager;
    private Button btPackage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneInfo=findViewById(R.id.phone_info);
        communicate=findViewById(R.id.communication_security);
        processManager=findViewById(R.id.process_management);
        virusKilling=findViewById(R.id.virus_killing);
        softManager=findViewById(R.id.sofeware_management);
        trafficManager=findViewById(R.id.traffic_statistics);
        cleanManager=findViewById(R.id.clear_cache);
        wifiManager=findViewById(R.id.advanced_tools);
        portManager=findViewById(R.id.set_center);
        btPackage=findViewById(R.id.button_package);

        phoneInfo.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this,PhoneInfoActivity.class);
            startActivity(intent);
        });
        communicate.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this,CommunicateActivity.class);
            startActivity(intent);
        });
        processManager.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this,ProcessManagerActivity.class);
            startActivity(intent);
        });

       virusKilling.setOnClickListener(v -> {
           Intent intent=new Intent(MainActivity.this,AntivirusActivity.class);
           startActivity(intent);
       });
        softManager.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this,SoftwareManagerActivity.class);
            startActivity(intent);
        });
        trafficManager.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, TrafficStatsSimpleActivity.class);
            startActivity(intent);
        });
        cleanManager.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, CleanCacheActivity.class);
            startActivity(intent);
        });
        wifiManager.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, WifiActivity.class);
            startActivity(intent);
        });
        portManager.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, PortActivity.class);
            startActivity(intent);
        });
        btPackage.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, MActivity.class);
            startActivity(intent);
        });

        //打开音量权限
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(
                    android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
        PermissionsUtils.initPermissions(this, new String[]{Manifest.permission.CALL_PHONE});

        //开启电话拦截服务
        Intent intent = new Intent(MainActivity.this, BlacklistInterceptService.class);
        startService(intent);

    }
}
